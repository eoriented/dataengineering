package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class SortWordCountUsingComparator extends Configured implements Tool {
    public static class SortMapper extends Mapper<Text, Text, LongWritable, Text> {
        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            context.write(new LongWritable(Long.parseLong(value.toString())), key);
        }
    }

    public static class CountComparator extends WritableComparator {
        protected CountComparator() {
            super(LongWritable.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            LongWritable l1 = (LongWritable) a;
            LongWritable l2 = (LongWritable) b;

            return -1 * l1.compareTo(l2);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "\t");

        Job job = Job.getInstance(conf, "SortWordCountUsingComparator");

        job.setJarByClass(SortWordCountUsingComparator.class);
        job.setMapperClass(SortMapper.class);
        job.setSortComparatorClass(CountComparator.class);
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new SortWordCountUsingComparator(), args);
        System.exit(exitCode);
    }
}
