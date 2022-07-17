package com.fastcampus.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

public class ScanExample {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf);
                Table table = connection.getTable(TableName.valueOf("myTable"))) {
            Scan scan = new Scan();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result ret : resultScanner) {
                System.out.println(ret);
            }
            resultScanner.close();
        }
    }
}