from pyspark.sql import SparkSession
from pyspark.sql.functions import *


if __name__ == "__main__":
    spark = SparkSession \
        .builder \
        .appName("StructuredWindowWordCountSql") \
        .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
  
    lines = spark \
        .readStream \
        .format("socket") \
        .option("host", "localhost") \
        .option("port", 9999) \
        .load()
        
    lines.createOrReplaceTempView("word_table")

    test = spark.sql("""
              SELECT window,
                     word,
                     COUNT(word)
              FROM (
                  SELECT explode(split(value, ' ')) AS word,
                         current_timestamp() AS ts
                  FROM word_table
              )
              WHERE word != ''
              GROUP BY window(ts, '10 seconds', '5 seconds'), word
              """)

    query = test \
    .writeStream \
    .outputMode("update") \
    .format("console") \
    .option("truncate", "false") \
    .start()

    query.awaitTermination()