# Apache Kafka

## 1. 설치
### 1) Docker 설치
다운로드 주소 : https://www.docker.com/products/docker-desktop/

### 2) docker-compose.yml 파일 작성
[docker-compose.yml](docker-compose.yml)

Kafka Cluster 시작하기
```bash
$ docker-compose up
```

삭제하기
```bash
$ docker-compose rm -fsv
```

## 2. Producer
### 1) 빌드
```bash
$ cd dataengineering/02-kafka/kafka-example
$ mvn clean package
```

### 2) 실행
```bash
$ java -cp target/kafka-example-1.0.0.jar com.fastcampus.kafka.KafkaProducerExample
```

## 3. Consumer
### 1) 실행
```bash
$ java -cp target/kafka-example-1.0.0.jar com.fastcampus.kafka.KafkaConsumerExample
```

## 4. Kafka Connect
### 0) 실습
file -> file source connector -> kafka -> console sink connector -> console

### 1) 사전 준비
#### Kafka download
```bash
$ wget https://dlcdn.apache.org/kafka/3.1.0/kafka_2.12-3.1.0.tgz
$ tar zxvf kafka_2.12-3.1.0.tgz
```

#### Kafka Cluster 실행
```bash
$ cd /path/to/dataengineering/02-ingestion/02-kafka
$ docker-compose up
```

토픽 생성 : connect-test

### 2) File Source Connector
#### Kafka connect standalone 모드 설정 파일 수정
connect-standalone1.properties
```bash
# 커넥트와 연동할 카프카 클러스터 호스트와 포트 지정
bootstrap.servers=localhost:9092

# 데이터를 카프카에 저장하거나 가져올 때 변환하는데 사용할 컨버터 지정
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter

# 스키마의 형태를 사용하고 싶으면 true 지정
key.converter.schemas.enable=false
value.converter.schemas.enable=false

# offset을 기록할 파일 위치
offset.storage.file.filename=/tmp/connect.offsets
# offset의 flush 주기
offset.flush.interval.ms=10000
```

#### 커넥터 설정 수정
connect-file-source.properties
```bash
# Connect 이름 설정
name=local-file-source
# 커넥터 클래스 이름 지정
connector.class=FileStreamSource
# 커넥터로 실행한 태스크 개수 지정
tasks.max=1
# 파일 위치
file=/path/to/test.txt
# 카프카 토픽 이름
topic=connect-test
```

#### Connect 실행
```bash
$ bin/connect-standalone.sh config/connect-standalone1.properties config/connect-file-source.properties
```

#### 수집할 파일 생성
```bash
$ echo "hello fastcampus" > /path/to/text.txt
$ echo "bigdata hadoop" >> /path/to/text.txt
$ echo "data platform" >> /path/to/text.txt
```

### 3) Console Sink Connector
#### Kafka connect standalone 모드 설정 파일 수정
connect-standalone2.properties
```bash
# 커넥트와 연동할 카프카 클러스터 호스트와 포트 지정
bootstrap.servers=localhost:9092

# 데이터를 카프카에 저장하거나 가져올 때 변환하는데 사용할 컨버터 지정
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter

# 스키마의 형태를 사용하고 싶으면 true 지정
key.converter.schemas.enable=false
value.converter.schemas.enable=false

# offset을 기록할 파일 위치
offset.storage.file.filename=/tmp/connect.offsets
# offset의 flush 주기
offset.flush.interval.ms=10000

# kafka connect listener 설정
listeners=http://localhost:18083
```

#### 커넥터 설정 수정
connect-console-sink.properties
```bash
# Connect 이름 설정
name=local-console-sink
# 커넥터 클래스 이름 지정
connector.class=org.apache.kafka.connect.file.FileStreamSinkConnector
# 커넥터로 실행한 태스크 개수 지정
tasks.max=1
# 카프카 토픽 이름
topics=connect-test
```

#### Connect 실행
```bash
$ bin/connect-standalone.sh config/connect-standalone2.properties config/connect-console-sink.properties
```

## 참고 자료
- [cp-kafka, apache kafka 호환성 확인](https://docs.confluent.io/platform/current/installation/versions-interoperability.html#cp-and-apache-ak-compatibility)
