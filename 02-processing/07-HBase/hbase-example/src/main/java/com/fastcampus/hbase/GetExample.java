package com.fastcampus.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class GetExample {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf);
                Table table = connection.getTable(TableName.valueOf("myTable"))) {
            Get get = new Get(Bytes.toBytes("row1"));
            // addColumn이나 addFamily로 특정 범위 지정 가능
            get.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("a"));

            Result result = table.get(get);
            byte[] val = result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("a"));
            System.out.println(Bytes.toString(val));
            System.out.println(Bytes.toString(result.value()));

            byte[] val2 = result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("b"));
            System.out.println(Bytes.toString(val2));
        }
    }
}