# Apache Spark

# Spark 설치
## 1. 사전 준비
- Java가 설치되어 있어야 합니다.
- python이 설치되어 있어야 합니다.

## 2. Spark Download
```
$ wget https://dlcdn.apache.org/spark/spark-3.2.1/spark-3.2.1-bin-hadoop3.2.tgz
$ tar zxvf spark-3.2.1-bin-hadoop3.2.tgz
```

## 3. Spark 환경 변수 등록
.zshrc
```
# 환경 변수 등록
export SPARK_HOME=/path/to/spark-3.2.1-bin-hadoop3.2
# PATH 설정
export PATH=$PATH:$SPARK_HOME/bin
```

## 3. PySpark 실행
```bash
$ pyspark
```

# 스파크 시작하기
### 1) SparkSession
SparkSession 확인
```
>>> spark
<pyspark.sql.session.SparkSession object at 0x109e6b280>
```

SparkSession으로 DataFrame 만들기
```
>>> sample = spark.range(1000).toDF("num")
>>> sample.show()
```

### 2) Transformation
Filter 적용하기
```
>>> evens = sample.filter("num % 2 = 0")
>>> evens.show()
```

### 3) Action
Count 실행
```
>>> sample.filter("num % 2 = 0").count()
500
```

데이터 보기
```
>>> sample.filter("num % 2 = 0").show()
```

### 4) Spark UI 접속
pyspark 실행시 아래와 같은 메시지가 출력됨
```
Spark context Web UI available at http://192.168.0.9:4040
```

http://localhost:4040 접속

# Spark RDD 예제
```python
# 데이터 읽기
rawRdd = spark.sparkContext.textFile("./dataset/MovieLens/movies.csv")
header = rawRdd.first()
filteredRawRdd = rawRdd.filter(lambda row: row != header) # filter out the header

# 데이터 출력
for r in filteredRawRdd.take(3):
    print(r)

# RDD 변환
movieRdd = filteredRawRdd.map(lambda x: x.split(","))

# filter
print(movieRdd.filter(lambda x: "Comedy" in x[2]).take(3))

# 정렬 1
print(movieRdd.sortBy(lambda x: x[0]).take(3))

# 정렬 2
print(movieRdd.sortBy(lambda x: int(x[0])).take(3))

# 저장
movieRdd.filter(lambda x: "Romance" in x[2]).sortBy(lambda x: -int(x[0])).saveAsTextFile("example1")

# repartition
movieRdd.filter(lambda x: "Romance" in x[2]).sortBy(lambda x: -int(x[0])).repartition(1).saveAsTextFile("example2")
```

# Spark WordCount
wordcount.py 파일 작성
```
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
 
```

spark job 제출
```
$ HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop spark-submit --master yarn --deploy-mode client --num-executors 2 --executor-memory 1G --executor-cores 1 --driver-memory 1G wordcount.py hdfs://localhost:9000/user/fastcampus/LICENSE.txt hdfs://localhost:9000/user/fastcampus/spark/output2
```

결과 확인
```
$ hadoop fs -ls /user/fastcampus/spark/output/part-00000
```

# Spark SQL 예제
## 1. 사전 준비
### 1) 데이터 셋 확인
- dataset/MovieLens/movies.csv
- dataset/MovieLens/ratings.csv

### 2) pyspark 실행
```
$ pyspark
```

### 3) data 읽기
스키마 없이 읽기
```python
movie_data_path = './dataset/MovieLens/movies.csv'
movie_df = spark.read.option("inferSchema", "true").option("header", "true").csv(movie_data_path)
movie_df.printSchema()
movie_df.take(3)
```

스키마 지정하여 읽기
```python
from pyspark.sql.types import *
from pyspark.sql.functions import *

schema = StructType([
    StructField("movieId", IntegerType(), nullable=True),
    StructField("title", StringType(), nullable=True),
    StructField("genres", StringType(), nullable=True)]
)
movie_data_path = './dataset/MovieLens/movies.csv'
movie_df = spark.read.option("header", "true").csv(movie_data_path, schema)
movie_df.printSchema()
movie_df.take(3)

movie_df.sort(col("title")).take(2)
movie_df.sort(col("title")).explain()

movie_df.filter(col("genres").contains("Romance")).select(col("title")).show()

ratings_path = "./dataset/MovieLens/ratings.csv"
rating_df = spark.read.option("header", "true").option("inferSchema", "true").csv(ratings_path)
rating_df.printSchema()

join_df = movie_df.join(rating_df, movie_df.movieId == rating_df.movieId, "inner")
join_df.select(col("title"), col("rating")).show(20, False)
join_df.select(col("title"), col("rating")).orderBy(col("rating").desc()).show(20, False)
```

### 4) SQL 사용하기
```python
movie_df.createOrReplaceTempView("movie")
rating_df.createOrReplaceTempView("rating")

# sql query
spark.sql("""
SELECT * 
FROM movie 
""").show(20, False)

# dataframe
movie_df.select("*").show(20, False)

sql = spark.sql("""
SELECT genres, count(*)
FROM movie 
GROUP BY genres
""") # transformation
sql.show(20, False) # action

df = movie_df.groupBy("genres").count()
df.show(20, False)

sql.explain()
df.explain()

sql = spark.sql("""
SELECT title, rating
FROM movie
JOIN rating ON movie.movieId = rating.movieId
""") # transformation
sql.show(20, False) # action
```