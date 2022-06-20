package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Arrays;

// 제너릭옵션 파서의 경우 분산 캐시를 지원하기 위한 files나 아카이브 옵션 등을 사용할 수 있습니다. 또한 하둡 conf 설정도 cli의 옵션으로 처리하여 실행할 수 있습니다.
public class GenericOptionsParserExample {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        Configuration conf = new Configuration();
        // conf안에 arg로 들어온 부분들을 셋팅해준다
        GenericOptionsParser optionsParser = new GenericOptionsParser(conf, args);
        String value1 = conf.get("mapreduce.map.memory.mb");
        boolean value2 = conf.getBoolean("job.test", false);
        System.out.println("value1 : " + value1 + " & value2 : " + value2);

        // GenericOptionsParser에서 옵션을 세팅하고 나머지 argument를 받아서 처리
        String[] remainingArgs = optionsParser.getRemainingArgs();
        System.out.println(Arrays.toString(remainingArgs));
    }
}
