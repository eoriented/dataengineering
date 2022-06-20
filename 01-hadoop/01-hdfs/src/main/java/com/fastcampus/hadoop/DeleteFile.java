package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public class DeleteFile {
    public static void main(String[] args) throws IOException {
        String uri = args[0];

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);

        Path path = new Path(uri);
        if (fs.exists(path)) {
            fs.delete(new Path(uri), false);
        }
    }
}
