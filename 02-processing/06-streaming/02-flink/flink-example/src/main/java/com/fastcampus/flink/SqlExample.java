package com.fastcampus.flink;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

public class SqlExample {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        tableEnv.executeSql("CREATE TEMPORARY TABLE SourceTable (f0 STRING) WITH ('connector' = 'datagen', 'rows-per-second' = '1')");
        tableEnv.executeSql("CREATE TEMPORARY TABLE SinkTable WITH ('connector' = 'print') LIKE SourceTable (EXCLUDING OPTIONS)");
        tableEnv.executeSql("CREATE TEMPORARY TABLE SinkTable2 (f1 BIGINT) WITH ('connector' = 'print')");

        Table table2 = tableEnv.from("SourceTable");

        // table2.insertInto("SinkTable").execute();
        table2.insertInto("SinkTable2").execute();
    }
}
