package com.fastcampus.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class PutExample {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            Table table = connection.getTable(TableName.valueOf("myTable"));
            Put put = new Put(Bytes.toBytes("row1"));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("a"), Bytes.toBytes("val1"));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("b"), Bytes.toBytes("val2"));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("c"), Bytes.toBytes("val3"));

            table.put(put);

            Put put2 = new Put(Bytes.toBytes("row2"));
            put2.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("a"), Bytes.toBytes("val4"));

            table.put(put2);
            table.close();
        }
    }
}
