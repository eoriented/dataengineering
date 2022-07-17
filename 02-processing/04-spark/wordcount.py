import sys
 
from pyspark import SparkContext
 
if __name__ == "__main__":
    argv = sys.argv
    sc = SparkContext(appName="Word Count")
    words = sc.textFile(argv[1]).flatMap(lambda line: line.split(" "))	
	# wordCounts = words.map(lambda word: (word, 1)).reduceByKey(lambda a,b:a +b)
    # 정렬 추가
    wordCounts = words.map(lambda word: (word, 1)).reduceByKey(lambda a,b:a +b).sortBy(lambda x: -x[1])
    wordCounts.saveAsTextFile(argv[2])
 