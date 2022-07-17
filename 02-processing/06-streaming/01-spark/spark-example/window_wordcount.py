from pyspark import SparkContext
from pyspark.streaming import StreamingContext

if __name__ == "__main__":
    sc = SparkContext("local[2]", "WindowWordCount")
    sc.setLogLevel("ERROR")
    ssc = StreamingContext(sc, 5)
    ssc.checkpoint("./tmp")

    lines = ssc.socketTextStream("localhost", 9999)
    counts = lines.flatMap(lambda line: line.split(" ")) \
                 .map(lambda word: (word, 1)) \
                 .reduceByKeyAndWindow(lambda x, y: x + y, 10, 5) # window size 10 seconds. window interval 5 seconds.
    counts.pprint()

    ssc.start()
    ssc.awaitTermination()