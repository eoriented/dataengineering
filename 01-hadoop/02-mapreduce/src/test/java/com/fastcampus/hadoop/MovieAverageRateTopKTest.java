package com.fastcampus.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class MovieAverageRateTopKTest {
    @Test
    public void movieMapTest() throws IOException {
        new MapDriver<Object, Text, Text, Text>()
                .withMapper(new MovieAverageRateTopK.MovieMapper())
                .withInput(new LongWritable(0), new Text("movieId,title,genres"))
                .withInput(new LongWritable(1), new Text("1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy"))
                .withInput(new LongWritable(2), new Text("2,Jumanji (1995),Adventure|Children|Fantasy"))
                .withOutput(new Text("1"), new Text("MToy Story (1995)"))
                .withOutput(new Text("2"), new Text("MJumanji (1995)"))
                .runTest();
    }

    @Test
    public void ratingMapTest() throws IOException {
        new MapDriver<Object, Text, Text, Text>()
                .withMapper(new MovieAverageRateTopK.RatingMapper())
                .withInput(new LongWritable(0), new Text("userId,movieId,rating,timestamp"))
                .withInput(new LongWritable(1), new Text("1,1,4.0,964982703"))
                .withInput(new LongWritable(2), new Text("7,1,4.5,1106635946"))
                .withInput(new LongWritable(3), new Text("8,2,4.0,839463806"))
                .withInput(new LongWritable(4), new Text("18,2,3.0,1455617462"))
                .withOutput(new Text("1"), new Text("R4.0"))
                .withOutput(new Text("1"), new Text("R4.5"))
                .withOutput(new Text("2"), new Text("R4.0"))
                .withOutput(new Text("2"), new Text("R3.0"))
                .runTest();
    }

    @Test
    public void movieRatingJoinReduceTest() throws IOException {
        new ReduceDriver<Text, Text, Text, Text>()
                .withReducer(new MovieAverageRateTopK.MovieRatingJoinReducer())
                .withInput(new Text("1"), Arrays.asList(new Text("MToy Story (1995)"), new Text("R4.0"), new Text("R4.5")))
                .withInput(new Text("2"), Arrays.asList(new Text("MJumanji (1995)"), new Text("R4.0"), new Text("R3.0")))
                .withOutput(new Text("Toy Story (1995)"), new Text("4.25"))
                .withOutput(new Text("Jumanji (1995)"), new Text("3.5"))
                .runTest();
    }

    @Test
    public void topKMapTest() throws IOException {
        new MapDriver<Object, Text, Text, Text>()
                .withMapper(new MovieAverageRateTopK.TopKMapper())
                .withInput(new LongWritable(0), new Text("Toy Story (1995)\t4.25"))
                .withInput(new LongWritable(1), new Text("Jumanji (1995)\t3.5"))
                .withOutput(new Text("3.5"), new Text("Jumanji (1995)"))
                .withOutput(new Text("4.25"), new Text("Toy Story (1995)"))
                .runTest();
    }

    @Test
    public void topKReduceTest() throws IOException {
        new ReduceDriver<Text, Text, Text, Text>()
                .withReducer(new MovieAverageRateTopK.TopKReducer())
                .withInput(new Text("3.5"), Arrays.asList(new Text("Jumanji (1995)")))
                .withInput(new Text("4.25"), Arrays.asList(new Text("Toy Story (1995)")))
                .withOutput(new Text("Toy Story (1995)"), new Text("4.25"))
                .withOutput(new Text("Jumanji (1995)"), new Text("3.5"))
                .runTest();
    }
}
