package com.fastcampus.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

public class WindowWordCountSql {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        DataStream<String> dataStream = env.socketTextStream("localhost", 9999);
        DataStream<String> wordDataStream = dataStream.flatMap(new Splitter());
        Table wordTable = tableEnv.fromDataStream(
            wordDataStream, 
            Schema.newBuilder()
                .columnByExpression("proc_time", "PROCTIME()")
                .build());

        tableEnv.createTemporaryView("word_table", wordTable);

        tableEnv.executeSql("SELECT window_end, f0 AS word, COUNT(f0) " + 
                            "FROM TABLE( " + 
                            "  TUMBLE(TABLE word_table, DESCRIPTOR(proc_time), INTERVAL '5' SECONDS)) " + 
                            "GROUP BY window_end, f0")
                .print();
    }

    public static class Splitter implements FlatMapFunction<String, String> {
        @Override
        public void flatMap(String sentence, Collector<String> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(word);
            }
        }
    }
}
