package com.fastcampus.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;

public class CreateTable {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf);
                Admin admin = connection.getAdmin()) {
            TableName table = TableName.valueOf("myTable");
            TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(table)
                    .setColumnFamily(ColumnFamilyDescriptorBuilder.of("cf"))
                    .build();
            
            admin.createTable(tableDescriptor);
        }
    }
}
