package com.fastcampus.flink;

import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;

public class TableApiExample {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
            .inStreamingMode()
            .build();
        
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        tableEnv.createTemporaryTable("SourceTable", 
            TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("f0", DataTypes.STRING())
                        .column("f1", DataTypes.BIGINT())
                        .build())
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 1L)
                .build()
            );

        tableEnv.createTemporaryTable("SinkTable", 
            TableDescriptor.forConnector("print")
                .schema(Schema.newBuilder()
                        .column("f0", DataTypes.STRING())
                        .column("f1", DataTypes.BIGINT())
                        .build())
                .build()
            );

        Table table1 = tableEnv.from("SourceTable");

        Table table2 = tableEnv.sqlQuery("SELECT * FROM SourceTable");

        table2.insertInto("SinkTable").execute();
    }
}
