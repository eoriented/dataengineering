package com.fastcampus.flink;

import static org.apache.flink.table.api.Expressions.*;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

public class WindowWordCountTable {
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
        
        wordTable
            .window(Tumble.over(lit(10).seconds()).on($("proc_time")).as("w"))
            .groupBy($("w"), $("f0"))
            .select($("w").end().as("window_end"), $("f0").as("word"), $("f0").count().as("cnt"))
            .execute()
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
