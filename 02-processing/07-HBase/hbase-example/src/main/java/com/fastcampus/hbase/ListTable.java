package com.fastcampus.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.TableDescriptor;

public class ListTable {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf);
                Admin admin = connection.getAdmin()) {
            List<TableDescriptor> listTableDescriptors =  admin.listTableDescriptors();

            for (TableDescriptor desc : listTableDescriptors) {
                System.out.println(desc.getTableName().getNameAsString());
            }
        }
    }
}
