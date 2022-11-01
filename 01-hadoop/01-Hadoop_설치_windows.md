# 하둡 설치 (Windows)

```
윈도우 환경에서 하둡을 실행하는 것은 권장하지 않습니다. 
윈도우 환경에서 조금 더 안정적으로 실습을 진행하길 원하신다면 Virtualbox나 VMWare와 같은 것을 이용하여 리눅스 환경을 구성하여 실습 환경을 구성하는 것을 추천드립니다
윈도우에서 리눅스 환경을 구성하신 후에 하둡 설치하는 방법은 Mac의 설치 방법과 거의 유사하니 참고 부탁드립니다.
```


## 1. Java 설치
### 1) 다운로드
- 아래의 사이트에 접속
  - https://www.oracle.com/java/technologies/downloads/#java8
  - Windows 탭을 클릭하여 자바 8 다운로드
- 다운로드 설치

- 설치 확인
```
$ java -version
```

## 2. Hadoop 다운로드
### 1) hadoop 다운로드
- hadoop.apache.org 접속
- Download > Binary 클릭
- hadoop download
- 다운로드 후 압축 풀기

## 3. 환경 변수 등록
- 시스템 속성 > 환경 변수
- 윈도우 환경 변수에 JAVA_HOME과 HADOOP_HOME 등록
- Path에 %HADOOP_HOME%/bin과 %JAVA_HOME%/bin 등록 
- 환경 변수 등록시 디렉토리에 띄어쓰기가 있으면 안됩니다.
  - java의 경우에는 `C:\Program Files` 하위에 존재하기 때문에 환경 변수 등록시 변경을 해야 합니다.
  - 그래서 설정할 때 `Program Files` 대신 `C:\Progra~1\Java\jdk1.8.0_xxx` 형태로 등록해주어야 합니다.

## 4. 설정 파일 수정
etc/hadoop/core-site.xml
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

etc/hadoop/hdfs-site.xml
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/hadoop-3.3.2/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/hadoop-3.3.2/dfs/data</value>
    </property>
</configuration>
```

```
* 참고
name dir과 data dir 생성
위에서 설정한 디렉토리와 동일한 위치에 생성
```

etc/hadoop/mapred-site.xml
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

etc/hadoop/yarn-site.xml
```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_HOME,PATH,LANG,TZ,HADOOP_MAPRED_HOME</value>
    </property>
</configuration>
```

## 5. hadoop winutils 다운로드
- https://github.com/kontext-tech/winutils 에서 하둡 버전에 맞는 winutils를 다운로드
- %HADOOP_HOME%/bin 디렉토리에 winutils 파일을 복사

## 6. HDFS 실행
네임 노드 포맷
```bash
$ hdfs namenode -format
```

hdfs 시작
```bash
$ sbin/start-dfs.sh
```

## 7. hdfs web ui 접속
http://localhost:9870

## 8. yarn 실행
```bash
$ sbin/start-yarn.sh
```

## 9. resource manage web ui
http://localhost:8088/

## 10. 예제 실행
```bash
$ hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.2.jar pi 16 10000
```
