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