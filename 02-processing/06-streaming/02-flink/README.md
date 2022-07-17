# Apache Flink
## 1. Flink 설치
### 1) 다운로드
```
$ wget https://dlcdn.apache.org/flink/flink-1.15.0/flink-1.15.0-bin-scala_2.12.tgz
$ tar zxvf flink-1.15.0-bin-scala_2.12.tgz
```

### 2) 환경변수 등록
.zshrc
```
export FLINK_HOME=/path/to/flink-1.15.0
export PATH=$PATH:$FLINK_HOME/bin
```

### 3) Flink 클러스터 실행
```
$ start-cluster.sh
```

### 4) Flink 예제 실행
Flink 예제 실행
```
$ flink run $FLINK_HOME/streaming/WordCount.jar
```

예제 결과 확인
```
$ tail $FLINK_HOMElog/flink-*-taskexecutor-*.out
```

### 5) Flink UI 접속 확인
http://localhost:8081

## 2. DataStreamExample
### 1) 프로젝트 빌드
```
$ cd /path/to/flink-example
$ mvn package
```

### 2) Flink 애플리케이션 실행
#### 1. 클러스터에 제출
```
$ flink run -c com.fastcampus.flink.DataStreamExample target/flink-example-1.0.0.jar
```

#### 2. IDE에서 실행
flink-clients가 포함되어있으면 바로 IDE에서도 실행 가능
```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-clients</artifactId>
  <version>${flink.version}</version>
</dependency>
```

## 3. WindowWordCount 예제 (DataStream API)
### 1) 프로젝트 빌드
```
$ cd /path/to/flink-example
$ mvn package
```

### 2) netcat 실행
```
$ nc -lk 9999
```

### 3) Flink 애플리케이션 실행
#### 1. Flink 클러스터에 제출
```
$ flink run -c com.fastcampus.flink.WindowWordCount target/flink-example-1.0.0.jar
```

#### 2. IDE에서 실행
Run Java 실행

### 4) 결과 확인
#### flink web ui 접속
http://localhost:8081

#### log로 확인
```
$ tail $FLINK_HOME/log/flink-*-taskexecutor-*.out
```

## 4. TableApiExample
### 1) 프로젝트 빌드
```
$ cd /path/to/flink-example
$ mvn package
```

### 2) Flink 애플리케이션 실행
#### 1. 클러스터에 제출
```
$ flink run -c com.fastcampus.flink.TableApiExample target/flink-example-1.0.0.jar
```

#### 2. IDE에서 실행
flink-clients가 포함되어있으면 바로 IDE에서도 실행 가능
```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-clients</artifactId>
    <version>${flink.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-table-planner_2.12</artifactId>
    <version>${flink.version}</version>
</dependency>
```

## 5. SqlExample
### 1) 프로젝트 빌드
```
$ cd /path/to/flink-example
$ mvn package
```

### 2) Flink 애플리케이션 실행
#### 1. 클러스터에 제출
```
$ flink run -c com.fastcampus.flink.SqlExample target/flink-example-1.0.0.jar
```

#### 2. IDE에서 실행
TableApiExample에 있는 dependencies와 동일

## 6. WindowWordCount 예제 (Table API)
### 1) 프로젝트 빌드
```
$ cd /path/to/flink-example
$ mvn package
```

### 2) netcat 실행
```
$ nc -lk 9999
```

### 3) Flink 애플리케이션 실행
#### 1. Flink 클러스터에 제출
```
$ flink run -c com.fastcampus.flink.WindowWordCountTable target/flink-example-1.0.0.jar
```

#### 2. IDE에서 실행
Run Java 실행

## 6. WindowWordCount 예제 (SQL)
### 1) 프로젝트 빌드
```
$ cd /path/to/flink-example
$ mvn package
```

### 2) netcat 실행
```
$ nc -lk 9999
```

### 3) Flink 애플리케이션 실행
#### 1. Flink 클러스터에 제출
```
$ flink run -c com.fastcampus.flink.WindowWordCountSql target/flink-example-1.0.0.jar
```

#### 2. IDE에서 실행
Run Java 실행