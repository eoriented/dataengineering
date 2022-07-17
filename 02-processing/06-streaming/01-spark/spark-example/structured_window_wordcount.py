from pyspark.sql import SparkSession
from pyspark.sql.functions import *


if __name__ == "__main__":
    spark = SparkSession \
        .builder \
        .appName("StructuredWindowWordCount") \
        .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
  
    lines = spark \
        .readStream \
        .format("socket") \
        .option("host", "localhost") \
        .option("port", 9999) \
        .load()

    words = lines.select(
        explode(
            split(lines.value, " ")
        ).alias("word"),
        current_timestamp().alias("ts")
    ) \
    .where("word != ''")

    wordCounts = words.groupBy(window(words.ts, "10 seconds", "5 seconds"), words.word).count()
    
    query = wordCounts \
    .writeStream \
    .outputMode("update") \
    .format("console") \
    .option("truncate", "false") \
    .start()

    query.awaitTermination()