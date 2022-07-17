package com.fastcampus.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public class DisableAndDropTable {
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        try (Connection connection = ConnectionFactory.createConnection(conf);
                Admin admin = connection.getAdmin()) {
            TableName table = TableName.valueOf("myTable");
            System.out.println("Table is enabled : " + admin.isTableEnabled(table));
            if (admin.isTableEnabled(table)) {
                admin.disableTable(table);
            }

            System.out.println("Table is enabled : " + admin.isTableEnabled(table));
            
            admin.deleteTable(table);
            System.out.println("tableExists: " + admin.tableExists(table));
        }
    }
}