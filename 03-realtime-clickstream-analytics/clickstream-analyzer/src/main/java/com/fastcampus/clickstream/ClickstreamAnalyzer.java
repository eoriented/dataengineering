package com.fastcampus.clickstream;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class ClickstreamAnalyzer {
    public enum DataType {
        ACTIVE_SESSION, ADS_PER_SECOND, REQUEST_PER_SECOND, ERROR_PER_SECOND
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment()
                .setParallelism(4);

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("weblog")
                .setGroupId("test1")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> dataStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "KafkaSource");
        DataStream<WebLog> webLogDataStream = dataStream.map(new WebLogMapFunction());
        webLogDataStream.print();

        DataStream<Tuple2<Long, Map<DataType, Integer>>> activeSessionDataStream = webLogDataStream
                .keyBy(t -> 1)
                .process(new ActiveSessionCountFunction())
                .map(new OutputMapFunction(DataType.ACTIVE_SESSION));

        DataStream<Tuple2<Long, Map<DataType, Integer>>> adsClickPerSecondDataStream = webLogDataStream
                .filter(l -> l.getUrl().startsWith("/ads"))
                .keyBy(t -> 1)
                .process(new RequestPerSecondFunction())
                .map(new OutputMapFunction(DataType.ADS_PER_SECOND));

        DataStream<Tuple2<Long, Map<DataType, Integer>>> requestPerSecondDataStream = webLogDataStream
                .keyBy(t -> 1)
                .process(new RequestPerSecondFunction())
                .map(new OutputMapFunction(DataType.REQUEST_PER_SECOND));

        DataStream<Tuple2<Long, Map<DataType, Integer>>> errorPerSecondDataStream = webLogDataStream
                .filter(l -> Integer.parseInt(l.getResponseCode()) >= 400)
                .keyBy(t -> 1)
                .process(new RequestPerSecondFunction())
                .map(new OutputMapFunction(DataType.ERROR_PER_SECOND));

        DataStream<Tuple2<Long, Map<DataType, Integer>>> resultDataStream = activeSessionDataStream
                .union(adsClickPerSecondDataStream)
                .union(requestPerSecondDataStream)
                .union(errorPerSecondDataStream)
                .keyBy(t -> t.f0)
                .reduce((value1, value2) -> {
                    value2.f1.forEach((key, value) -> value1.f1.merge(key, value, (v1, v2) -> v1 >= v2 ? v1 : v2));
                    return value1;
                });
        resultDataStream.print();

        resultDataStream.addSink(
                JdbcSink.sink(
                        "REPLACE INTO stats (ts, active_session, ads_per_second, request_per_second, error_per_second) values (?, ?, ?, ?, ?)",
                        (statement, tuple) -> {
                            Timestamp timestamp = new Timestamp(tuple.f0);
                            statement.setTimestamp(1, timestamp);
                            statement.setInt(2, tuple.f1.getOrDefault(DataType.ACTIVE_SESSION, 0));
                            statement.setInt(3, tuple.f1.getOrDefault(DataType.ADS_PER_SECOND, 0));
                            statement.setInt(4, tuple.f1.getOrDefault(DataType.REQUEST_PER_SECOND, 0));
                            statement.setInt(5, tuple.f1.getOrDefault(DataType.ERROR_PER_SECOND, 0));
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(200)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://localhost:3306/clickstream")
                                .withDriverName("com.mysql.cj.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("fastcampus")
                                .build()
                )
        );

        env.execute("Clickstream Analyzer");
    }

    public static class WebLogMapFunction implements MapFunction<String, WebLog> {
        @Override
        public WebLog map(String value) throws Exception {
            String[] tokens = value.split(" ");
            return new WebLog(tokens[0], Instant.parse(tokens[1]).toEpochMilli(), tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
        }
    }

    public static class ActiveSessionCountFunction extends KeyedProcessFunction<Integer, WebLog, Tuple2<Long, Integer>> {
        private transient MapState<String, Long> sessionMapState;
        private transient ValueState<Long> timerValueState;
        private static final long INTERVAL = 1000;
        private static final long SESSION_TIMEOUT = 30 * 1000;

        @Override
        public void open(Configuration parameters) throws Exception {
            MapStateDescriptor<String, Long> mapStateDescriptor =
                    new MapStateDescriptor<>("sessionMap", String.class, Long.class);
            sessionMapState = getRuntimeContext().getMapState(mapStateDescriptor);

            ValueStateDescriptor<Long> valueStateDescriptor =
                    new ValueStateDescriptor<>("firetime", TypeInformation.of(new TypeHint<Long>() {}));
            timerValueState = getRuntimeContext().getState(valueStateDescriptor);
        }

        @Override
        public void processElement(WebLog log,
                                   KeyedProcessFunction<Integer, WebLog, Tuple2<Long, Integer>>.Context ctx,
                                   Collector<Tuple2<Long, Integer>> out) throws Exception {
            if (System.currentTimeMillis() - log.getTimestamp() <= SESSION_TIMEOUT) {
                sessionMapState.put(log.getSessionId(), log.getTimestamp());
            }

            long timestamp = ctx.timerService().currentProcessingTime();
            if (null == timerValueState.value()) {
                long nextTimerTimestamp = timestamp - (timestamp % INTERVAL) + INTERVAL;
                timerValueState.update(nextTimerTimestamp);
                ctx.timerService().registerProcessingTimeTimer(nextTimerTimestamp);
            }
        }

        @Override
        public void onTimer(long timestamp,
                            KeyedProcessFunction<Integer, WebLog, Tuple2<Long, Integer>>.OnTimerContext ctx,
                            Collector<Tuple2<Long, Integer>> out) throws Exception {
            int size = 0;
            for (Map.Entry<String, Long> session : sessionMapState.entries()) {
                if (System.currentTimeMillis() - session.getValue() <= SESSION_TIMEOUT) {
                    size++;
                }
            }

            long currentProcessingTime = (ctx.timerService().currentProcessingTime() / 1000) * 1000;
            out.collect(Tuple2.of(currentProcessingTime, size));

            long nextTimerTimestamp = timestamp + INTERVAL;
            ctx.timerService().registerProcessingTimeTimer(nextTimerTimestamp);
        }
    }

    public static class OutputMapFunction implements MapFunction<Tuple2<Long, Integer>, Tuple2<Long, Map<DataType, Integer>>> {
        private DataType dataType;

        public OutputMapFunction(DataType dataType) {
            this.dataType = dataType;
        }

        @Override
        public Tuple2<Long, Map<DataType, Integer>> map(Tuple2<Long, Integer> value) throws Exception {
            Map<DataType, Integer> map = new HashMap<>();
            map.put(dataType, value.f1);
            return Tuple2.of(value.f0, map);
        }
    }

    public static class RequestPerSecondFunction extends KeyedProcessFunction<Integer, WebLog, Tuple2<Long, Integer>> {
        private transient ValueState<Long> timerValueState;
        private transient ValueState<Integer> countState;
        private static final long INTERVAL = 1000;

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<Long> valueStateDescriptor =
                    new ValueStateDescriptor<>("firetime", TypeInformation.of(new TypeHint<Long>() {}));
            timerValueState = getRuntimeContext().getState(valueStateDescriptor);

            ValueStateDescriptor<Integer> countStateDescriptor =
                    new ValueStateDescriptor<>("requestCount", TypeInformation.of(new TypeHint<Integer>() {}));
            countState = getRuntimeContext().getState(countStateDescriptor);
        }

        @Override
        public void processElement(WebLog value,
                                   KeyedProcessFunction<Integer, WebLog, Tuple2<Long, Integer>>.Context ctx,
                                   Collector<Tuple2<Long, Integer>> out) throws Exception {
            long timestamp = ctx.timerService().currentProcessingTime();
            long nextTimerTimestamp = timestamp - (timestamp % INTERVAL) + INTERVAL;
            if (timerValueState.value() == null) {
                timerValueState.update(nextTimerTimestamp);
                ctx.timerService().registerProcessingTimeTimer(nextTimerTimestamp);
            }

            if (countState.value() == null) {
                countState.update(0);
            }

            if (nextTimerTimestamp - INTERVAL <= value.getTimestamp() && value.getTimestamp() < nextTimerTimestamp) {
                countState.update(countState.value() + 1);
            }
        }

        @Override
        public void onTimer(long timestamp,
                            KeyedProcessFunction<Integer, WebLog, Tuple2<Long, Integer>>.OnTimerContext ctx,
                            Collector<Tuple2<Long, Integer>> out) throws Exception {
            long currentProcessingTime = (ctx.timerService().currentProcessingTime() / 1000) * 1000;

            out.collect(Tuple2.of(currentProcessingTime, countState.value()));
            countState.update(0);

            long nextTimerTimestamp = timestamp + INTERVAL;
            ctx.timerService().registerProcessingTimeTimer(nextTimerTimestamp);
        }
    }
}
