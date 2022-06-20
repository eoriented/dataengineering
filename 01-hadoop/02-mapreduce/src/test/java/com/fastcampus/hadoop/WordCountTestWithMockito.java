package com.fastcampus.hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Test;

import com.fastcampus.hadoop.WordCount.TokenizerMapper;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class WordCountTestWithMockito {
    @Test
    public void wordCountMapTest() throws IOException, InterruptedException {
        TokenizerMapper mapper = new TokenizerMapper();
        Mapper.Context context = mock(Mapper.Context.class);
        mapper.word = mock(Text.class);
        mapper.map(new LongWritable(0), new Text("dog dog cat"), context);
        // 동작 확인 순서를 위해 inOrder 메서드 사용
        InOrder inOrder = inOrder(mapper.word, context);
        inOrder.verify(mapper.word).set(eq("dog"));
        inOrder.verify(context).write(eq(mapper.word), eq(new IntWritable(1)));
        inOrder.verify(mapper.word).set(eq("dog"));
        inOrder.verify(context).write(eq(mapper.word), eq(new IntWritable(1)));
        inOrder.verify(mapper.word).set(eq("cat"));
        inOrder.verify(context).write(eq(mapper.word), eq(new IntWritable(1)));
    }

    @Test
    public void wordCountReduceTest() throws IOException, InterruptedException {
        WordCount.IntSumReducer reducer = new WordCount.IntSumReducer();
        Reducer.Context context = mock(Reducer.Context.class);

        List<IntWritable> values = Arrays.asList(new IntWritable(1), new IntWritable(1));

        reducer.reduce(new Text("dog"), values, context);

        verify(context).write(new Text("dog"), new IntWritable(2));
    }

    @Test
    public void counterTest() throws IOException, InterruptedException {
        WordCountWithCounters.TokenizerMapper mapper = new WordCountWithCounters.TokenizerMapper();
        Mapper.Context context = mock(Mapper.Context.class);
        Counter counter = mock(Counter.class);
        when(context.getCounter(WordCountWithCounters.Word.WITHOUT_SPECIAL_CHARACTER)).thenReturn(counter);

        mapper.map(new LongWritable(0), new Text("dog dog cat"), context);

        verify(counter, times(3)).increment(1);
    }
}
