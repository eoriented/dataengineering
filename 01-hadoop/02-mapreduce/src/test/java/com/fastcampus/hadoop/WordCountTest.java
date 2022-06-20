package com.fastcampus.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WordCountTest {
    MapDriver<Object, Text, Text, IntWritable> mapDriver;
    ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
    MapReduceDriver<Object, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;

    @Before
    public void setUp() {
        mapDriver = new MapDriver<>(new WordCount.TokenizerMapper());
        reduceDriver = new ReduceDriver<>(new WordCount.IntSumReducer());
        mapReduceDriver = new MapReduceDriver<>(new WordCount.TokenizerMapper(), new WordCount.IntSumReducer());
    }

    @Test
    public void wordCountMapTest() throws IOException {
        new MapDriver<Object, Text, Text, IntWritable>()
            .withMapper(new WordCount.TokenizerMapper())
            .withInput(new LongWritable(0L), new Text("dog dog cat cat owl cat"))
            .withOutput(new Text("dog"), new IntWritable(1))
            .withOutput(new Text("dog"), new IntWritable(1))
            .withOutput(new Text("cat"), new IntWritable(1))
            .withOutput(new Text("cat"), new IntWritable(1))
            .withOutput(new Text("owl"), new IntWritable(1))
            .withOutput(new Text("cat"), new IntWritable(1))
            .runTest();
    }

    // 안녕하세요 mrunit 실습 두번 째 시간입니다.
    // 이전에 매퍼와 리듀스에 대한 테스트 실습을 진행하였는데요.
    // runtest를 통하면 바로 입력값과 출력값을 비교하여 검증을 해줍니다.
    // 그러나 runTest 외에도 run이라는 함수를 통해 입력에 대한 처리 결과 값을 리스트 값으로 리턴해주어
    // 해당 값을 직접 출력해보거나 사용할 수 있는데요. 한번 실습을 통해 진행해보도록 하겠습니다.
    // 먼저 맵 드라이버를 다시 선언해야 하는데요. 주로 맵 드라이버나 리듀스 드라이버의 경우 테스트
    // 파일 내에서 많이 사용하기 때문에 멤버변수로 선언하도록 하겠습니다.
    @Test
    public void wordCountMapTest2() throws IOException {
        List<Pair<Text, IntWritable>> result = mapDriver
            .withInput(new LongWritable(0L), new Text("dog dog cat cat owl cat"))
            .run();

        System.out.println(result);
    }

    @Test
    public void wordCountReduceTest() throws IOException {
        new ReduceDriver<Text, IntWritable, Text, IntWritable>()
                .withReducer(new WordCount.IntSumReducer())
                .withInput(new Text("dog"), Arrays.asList(new IntWritable(1), new IntWritable(1)))
                .withInput(new Text("cat"), Arrays.asList(new IntWritable(1), new IntWritable(1), new IntWritable(1)))
                .withInput(new Text("owl"), Arrays.asList(new IntWritable(1)))
                .withOutput(new Text("dog"), new IntWritable(2))
                .withOutput(new Text("cat"), new IntWritable(3))
                .withOutput(new Text("owl"), new IntWritable(1))
                .runTest();
    }

    @Test
    public void wordCountReduceTest2() throws IOException {
        List<Pair<Text, IntWritable>> result = reduceDriver
                .withInput(new Text("dog"), Arrays.asList(new IntWritable(1), new IntWritable(1)))
                .withInput(new Text("cat"), Arrays.asList(new IntWritable(1), new IntWritable(1), new IntWritable(1)))
                .withInput(new Text("owl"), Arrays.asList(new IntWritable(1)))
                .run();
        System.out.println(result);
    }

    @Test
    public void wordCountTest() throws IOException {
        mapReduceDriver
                .withInput(new LongWritable(0L), new Text("dog dog cat cat owl cat"))
                .withOutput(new Text("cat"), new IntWritable(3))
                .withOutput(new Text("dog"), new IntWritable(2))
                .withOutput(new Text("owl"), new IntWritable(1))
                .runTest();
    }

    @Test
    public void wordCountTest2() throws IOException {
        List<Pair<Text, IntWritable>> result = mapReduceDriver
                .withInput(new LongWritable(0L), new Text("dog dog cat cat owl cat"))
                .run();
        System.out.println(result);
    }

    @Test
    public void wordCountWithCountTest() throws IOException {
        MapDriver<Object, Text, Text, IntWritable> mapDriver1 = new MapDriver<>(new WordCountWithCounters.TokenizerMapper());
        mapDriver1.withInput(new LongWritable(0L), new Text("'hello' 'world' fastcampus hadoop !!"))
                .run();
        System.out.println(mapDriver1.getCounters().findCounter(WordCountWithCounters.Word.WITHOUT_SPECIAL_CHARACTER).getValue());
        assertEquals(mapDriver1.getCounters().findCounter(WordCountWithCounters.Word.WITHOUT_SPECIAL_CHARACTER).getValue(), 2);
        System.out.println(mapDriver1.getCounters().findCounter(WordCountWithCounters.Word.WITH_SPECIAL_CHARACTER).getValue());
        assertEquals(mapDriver1.getCounters().findCounter(WordCountWithCounters.Word.WITH_SPECIAL_CHARACTER).getValue(), 3);
    }
}