# Apache Flume

## 1. Flume 설치
Flume 다운로드
```bash
$ wget https://dlcdn.apache.org/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz
```

압축 해제
```bash
$ tar zxvf apache-flume-1.9.0-bin.tar.gz
```

## 2. Flume 실습 1
netcat의 소스를을 logger 싱크로 보내기

### 1) Flume 설정 파일 만들기
example.conf
```bash
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44445

# Describe the sink
a1.sinks.k1.type = logger

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```

### 2) Flume agent 실행
```bash
$ bin/flume-ng agent --conf conf --conf-file example.conf --name a1 -Dflume.root.logger=INFO,console
```
- --conf : flume의 conf directory
- --conf-file : flume agent 설정 파일 지정
- --name : agent 이름
- -Dproperty=value : java property value

예제에서 logger를 통해 출력을 확인하기 위해서 `-Dflume.root.logger=INFO,console`이 필요

### 3) netcat을 이용하여 Flume Source로 데이터 전달
```bash
$ nc localhost 44444
hello world
hello fastcampus
```

### 4) Flume의 Console log 결과 확인
```
[INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 68 65 6C 6C 6F 20 77 6F 72 6C 64                hello world }
[INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:95)] Event: { headers:{} body: 68 65 6C 6C 6F 20 66 61 73 74 63 61 6D 70 75 73 hello fastcampus }
```

## 3. Flume 실습 2
Flume의 log를 hdfs와 local file system에 전송하는 예제

### 0) 사전 준비
#### Hadoop cluster 실행
```bash
$ $HADOOP_HOME/sbin/start-all.sh
```

### 1) Flume 설정 파일 만들기
flume-log-source-agent.conf
```bash
# http://flume.apache.org/FlumeUserGuide.html#exec-source
source_agent.sources = flume_log
source_agent.sources.flume_log.type = exec
source_agent.sources.flume_log.command = tail -f $FLUME_HOME/logs/flume.log
source_agent.sources.flume_log.batchSize = 1
source_agent.sources.flume_log.channels = memoryChannel
source_agent.sources.flume_log.interceptors = itime

# http://flume.apache.org/FlumeUserGuide.html#timestamp-interceptor
source_agent.sources.flume_log.interceptors.itime.type = timestamp

# http://flume.apache.org/FlumeUserGuide.html#memory-channel
source_agent.channels = memoryChannel
source_agent.channels.memoryChannel.type = memory
source_agent.channels.memoryChannel.capacity = 100

## Send to Flume Collector on Hadoop Node
# http://flume.apache.org/FlumeUserGuide.html#avro-sink
source_agent.sinks = avro_sink
source_agent.sinks.avro_sink.type = avro
source_agent.sinks.avro_sink.channel = memoryChannel
source_agent.sinks.avro_sink.hostname = localhost
source_agent.sinks.avro_sink.port = 44444
```

flume-log-target-agent.conf
```bash
# http://flume.apache.org/FlumeUserGuide.html#avro-source
target_agent.sources = AvroIn
target_agent.sources.AvroIn.type = avro
target_agent.sources.AvroIn.bind = 0.0.0.0
target_agent.sources.AvroIn.port = 44444
target_agent.sources.AvroIn.channels = mc1 mc2

## Channels ##
## Source writes to 2 channels, one for each sink
target_agent.channels = mc1 mc2

# http://flume.apache.org/FlumeUserGuide.html#memory-channel

target_agent.channels.mc1.type = memory
target_agent.channels.mc1.capacity = 100

target_agent.channels.mc2.type = memory
target_agent.channels.mc2.capacity = 100

## Sinks ##
target_agent.sinks = LocalOut HdfsOut

## Write copy to Local Filesystem 
# http://flume.apache.org/FlumeUserGuide.html#file-roll-sink
target_agent.sinks.LocalOut.type = file_roll
target_agent.sinks.LocalOut.sink.directory = /path/to/var/log/flume-log
target_agent.sinks.LocalOut.sink.rollInterval = 0
target_agent.sinks.LocalOut.channel = mc1

## Write to HDFS
# http://flume.apache.org/FlumeUserGuide.html#hdfs-sink
target_agent.sinks.HdfsOut.type = hdfs
target_agent.sinks.HdfsOut.channel = mc2
target_agent.sinks.HdfsOut.hdfs.path = hdfs://<ip_address>:9000/user/fastcampus/flume/%y%m%d
target_agent.sinks.HdfsOut.hdfs.fileType = DataStream
target_agent.sinks.HdfsOut.hdfs.writeFormat = Text
target_agent.sinks.HdfsOut.hdfs.rollSize = 0
target_agent.sinks.HdfsOut.hdfs.rollCount = 10000
target_agent.sinks.HdfsOut.hdfs.rollInterval = 600
```

### 2) Sink Directory 생성
```bash
# local file system
$ mkdir -p /path/to/var/log/flume-log

# hdfs
$ hadoop fs -mkdir -p /user/fastcampus/flume
```

### 3) Flume Agent 실행
```bash
# target agent 실행
$ bin/flume-ng agent --conf conf --conf-file flume-log-target-agent.conf --name target_agent

# source agent 실행
$ bin/flume-ng agent --conf conf --conf-file flume-log-source-agent.conf --name source_agent
```

### 4) 결과 확인
- local file system
```bash
$ cat /path/to/var/log/flume-log/<timestamp>-1
```

- hdfs
```bash
$ hadoop fs -cat /user/fastcampus/FlumeData.<timestamp>-1
```
