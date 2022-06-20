package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;

public class ToolRunnerExample extends Configured implements Tool {

    @Override
    public int run(String[] args) {
        Configuration conf = getConf();
        String value1 = conf.get("mapreduce.map.memory.mb");
        boolean value2 = conf.getBoolean("job.test", false);
        System.out.println("value1 : " + value1 + " & value2 : " + value2);

        // remain arguments
        System.out.println(Arrays.toString(args));
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new ToolRunnerExample(), args);
        System.exit(exitCode);
    }
}
