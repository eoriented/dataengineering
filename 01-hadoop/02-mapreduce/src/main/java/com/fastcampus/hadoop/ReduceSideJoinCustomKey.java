package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Iterator;

public class ReduceSideJoinCustomKey extends Configured implements Tool {
    static enum DataType {
        DEPARTMENT("a"), EMPLOYEE("b");

        DataType(String value) {
            this.value = value;
        }
        private final String value;
        public String value() { return value; }
    }

    public static class EmployeeMapper extends Mapper<LongWritable, Text, TextText, Text> {
        TextText outKey = new TextText();
        Text outValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // emp_no, birth_date, first_name, last_name, gender, hire_date, dept_no
            String[] split = value.toString().split(",");

            // 키값은 (dept_no, 구분필드)
            outKey.set(new Text(split[6]), new Text(DataType.EMPLOYEE.value()));
            outValue.set(split[0] + "\t" + split[2] + "\t" + split[4]);
            context.write(outKey, outValue);
        }
    }

    public static class DepartmentMapper extends Mapper<LongWritable, Text, TextText, Text> {
        TextText outKey = new TextText();
        Text outValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(",");
            outKey.set(new Text(split[0]), new Text(DataType.DEPARTMENT.value()));
            outValue.set(split[1]);
            context.write(outKey, outValue);
        }
    }

    public static class ReduceJoinReducer extends Reducer<TextText, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void reduce(TextText key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Iterator<Text> iter = values.iterator();

            String departmentText = iter.next().toString();

            while (iter.hasNext()) {
                Text employeeText = iter.next();
                String[] employeeSplit = employeeText.toString().split("\t");
                outKey.set(employeeSplit[0]);
                outValue.set(employeeSplit[1] + "\t" + employeeSplit[2] + "\t" + departmentText);
                context.write(outKey, outValue);
            }

        }
    }

    public static class KeyPartitioner extends Partitioner<TextText, Text> {
        @Override
        public int getPartition(TextText key, Text value, int numPartitions) {
            // & (bitwise) 연산을 하는 이유는 partition number가 양수여야 하기 때문에 다음과 같이 지정
            return (key.getFirst().hashCode() & Integer.MAX_VALUE) % numPartitions;
        }
    }

    public static class KeyComparator extends WritableComparator {
        protected KeyComparator() {
            super(TextText.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            TextText t1 = (TextText) a;
            TextText t2 = (TextText) b;
            int cmp = t1.getFirst().compareTo(t2.getFirst());
            if (cmp != 0) {
                return cmp;
            }
            return t1.getSecond().compareTo(t2.getSecond());
        }
    }

    public static class GroupComparator extends WritableComparator {
        protected GroupComparator() {
            super(TextText.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            TextText t1 = (TextText) a;
            TextText t2 = (TextText) b;
            return t1.getFirst().compareTo(t2.getFirst());
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf(), "ReduceSideJoinCustomKey");

        job.setJarByClass(ReduceSideJoinCustomKey.class);
        job.setReducerClass(ReduceJoinReducer.class);
        job.setMapOutputKeyClass(TextText.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setPartitionerClass(KeyPartitioner.class);
        job.setSortComparatorClass(KeyComparator.class);
        job.setGroupingComparatorClass(GroupComparator.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, EmployeeMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, DepartmentMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new ReduceSideJoinCustomKey(), args);
        System.exit(exitCode);
    }
}
