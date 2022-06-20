package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class MapSideJoinDistCache extends Configured implements Tool {
    public static class MapSideJoinMapper extends Mapper<LongWritable, Text, Text, Text> {
        HashMap<String, String> departmentMap = new HashMap<>();
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            URI[] uris = context.getCacheFiles();
            for (URI uri : uris) {
                Path path = new Path(uri.getPath());
                loadDepartmentMap(path.getName());
            }
        }

        private void loadDepartmentMap(String fileName) {
            String line = "";
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                while ((line = br.readLine()) != null) {
                    // dept_no, dept_name
                    String[] split = line.split(",");
                    departmentMap.put(split[0], split[1]);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            // emp_no, birth_date, first_name, last_name, gender, hire_date, dept_no
            String[] split = value.toString().split(",");

            outKey.set(split[0]);
            String deparment = departmentMap.get(split[6]);
            deparment = deparment == null ? "Not Found" : deparment;
            outValue.set(split[2] + "\t" + split[4] + "\t" + deparment);
            context.write(outKey, outValue);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf(), "MapSideJoinDistCache");
        // 분산 캐시 추가
        job.addCacheFile(new URI("/user/fastcampus/join/input/departments"));

        job.setJarByClass(MapSideJoinDistCache.class);

        job.setMapperClass(MapSideJoinMapper.class);
        job.setNumReduceTasks(0);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MapSideJoinDistCache(), args);
        System.exit(exitCode);
    }
}
