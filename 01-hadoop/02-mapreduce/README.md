# MapReduce 실습
## 0. 사전 준비
- hadoop이 실행 중이어야 합니다.
- Maven이 설치되어 있어야 합니다.

## 1. WordCount
WordCount 실습

### 1) 실행
```bash
$ hadoop jar target/wordcount-1.0.jar com.fastcampus.hadoop.WordCount /user/fastcampus/input/LICENSE.txt /user/fastcampus/output
```

### 2) 결과 확인
```bash
$ hadoop fs -cat /user/fastcampus/output/part-r-00000
```

## 2. Counter 예제
WordCount 예제에 Counter 사용

### 1) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.WordCountWithCounters /user/fastcampus/input/LICENSE.txt /user/fastcampus/output
```

### 2) 결과 확인

## 3. 정렬 - 1
MapReduce에서 제공하는 정렬 기능을 사용합니다.
기존의 MapReduce의 wordcount의 아웃풋을 입력 데이터로 활동합니다.

### 1) 입력 파일 확인
```bash
$ hadoop fs -tail /user/fastcampus/output
```

### 2) 맵리듀스 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/SortWordCount.java

### 3) 빌드
```bash
$ mvn package
```

### 4) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.SortWordCount /user/fastcampus/output /user/fastcampus/sortoutput
```

### 5) 결과 확인
```bash
$ hadoop fs -cat /user/fastcampus/sortoutput/part-r-00000
```

## 4. 정렬 - 2
Comparator를 이용하여 내림차순으로 정렬하는 예제

### 1) 입력 파일 확인
```bash
$ hadoop fs -tail /user/fastcampus/output
```

### 2) 맵리듀스 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/SortWordCountUsing.java

### 3) 빌드
```bash
$ mvn package
```

### 4) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.SortWordCountUsingComparator /user/fastcampus/output /user/fastcampus/sortoutput2
```

### 5) 결과 확인
```bash
$ hadoop fs -cat /user/fastcampus/sortoutput2/part-r-00000
```

## 5. Map-side Join
### 1) 입력 파일 업로드
```bash
$ hadoop fs -mkdir -p /user/fastcampus/join/input
$ hadoop fs -put dataset/employees /user/fastcampus/join/input
$ hadoop fs -put dataset/departments /user/fastcampus/join/input
```

### 2) 맵리듀스 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/MapSideJoinDistCache.java

### 3) 빌드
```bash
$ mvn package
```

### 4) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.MapSideJoinDistCache /user/fastcampus/join/input/employees /user/fastcampus/joinoutput1
```

### 5) 결과 확인
```bash
$ hadoop fs -head /user/fastcampus/joinoutput1/part-m-00000
```

## 6. Reduce-side Join 1
### 1) 맵리듀스 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/ReduceSideJoin.java

### 2) 빌드
```bash
$ mvn package
```

### 3) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.ReduceSideJoin /user/fastcampus/join/input/employees /user/fastcampus/join/input/departments /user/fastcampus/joinoutput2
```

## 7. Reduce-side Join 2
### 1) 맵리듀스 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/ReduceSideJoinCustomKey.java

### 2) 빌드
```bash
$ mvn package
```

### 3) 실행
```bash
hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.ReduceSideJoinCustomKey /user/fastcampus/join/input/employees /user/fastcampus/join/input/departments /user/fastcampus/joinoutput3
```

## 8. GenericOptionParser
### 1) 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/GenericOptionsParserExample.java

### 2) 빌드
```bash
$ mvn package
```

### 3) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.GenericOptionsParserExample -files hdfs://localhost:9000/user/fastcampus/join/input/departments -Dmapreduce.map.memory.mb=4g -Djob.test=true other1 other2
```

## 9. ToolRunner
### 1) 프로그램 작성
01-hadoop/02-mapreduce/src/main/java/com/fastcampus/hadoop/ToolRunnerExample.java

### 2) 빌드
```bash
$ mvn package
```

### 3) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.ToolRunnerExample -Dmapreduce.map.memory.mb=4g -Djob.test=true other1 other2
```

## 10. MRUnit
### 1) 의존성 추가
프로젝트 pom.xml에 의존성 추가
```xml
<dependency>
    <groupId>org.apache.mrunit</groupId>
    <artifactId>mrunit</artifactId>
    <version>1.1.0</version>
    <classifier>hadoop2</classifier>
    <scope>test</scope>
</dependency>
```

### 2) 테스트 파일 작성
dataengineering/01-hadoop/02-mapreduce/src/test/java/com/fastcampus/hadoop/WordCountTest.java

## 11. Mockito를 이용한 테스트
dataengineering/01-hadoop/02-mapreduce/src/test/java/com/fastcampus/hadoop/WordCountTestWithMockito.java

## 12. MapReduce 실전 예제 - 영화 평점 Top 30
### 1) 사전 준비
- Hadoop cluster가 동작 중이어야 합니다.

### 2) dataset 업로드
```bash
$ hadoop fs -mkdir -p /user/fastcampus/movie/input
$ hadoop fs -put dataset/MovieLens/movies.csv dataset/MovieLens/ratings.csv /user/fastcampus/movie/input
```

### 3) 빌드
```bash
$ mvn package
```

### 4) 실행
```bash
$ hadoop jar target/mapreduce-example-1.0.0.jar com.fastcampus.hadoop.MovieAverageRateTopK /user/fastcampus/movie/input/movies.csv /user/fastcampus/movie/input/ratings.csv /user/fastcampus/movie/output/first /user/fastcampus/movie/output/second
```