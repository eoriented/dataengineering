package com.fastcampus.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.TableDescriptor;

public class DescriptionTable {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf);
                Admin admin = connection.getAdmin()) {
            TableDescriptor tableDescriptor = admin.getDescriptor(TableName.valueOf("myTable"));
            System.out.println(tableDescriptor);
        }
    }
}
