# 하둡 설치 (Windows)
## 1. Virtualbox를 이용한 Ubuntu 설치
### 1) Virtualbox 다운로드
- [사이트](https://www.virtualbox.org/wiki/Downloads) 접속
  - Windows hosts 클릭하여 다운로드
- 다운로드한 파일 설치

### 2) Ubuntu 다운로드
- [사이트](https://ubuntu.com/download/desktop) 접속
  - 22.04.1 LTS 버전 다운로드

### 3) Virtualbox 실행
- 새로 만들기
  - Skip unattended installation
  - 메모리 4096MB 이상, 프로세서 2개 이상, 디스크 25GB
  - Try or Install ubuntu
  - Install ubuntu
  - Normal installation

## 2. Java 설치
- 설치
```
$ sudo apt-get update
$ sudo apt-get install openjdk-8-jdk
```

- 설치 확인
```
$ java -version
```

~/.bashrc 파일
```bash
# JAVA_HOME 환경 변수 추가
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

설정 적용
```bash
$ source ~/.bashrc
```

## 3. Maven 설치
- 설치
```
$ sudo apt-get install maven
```

- 설치 확인
```
$ mvn -version
```

## 4. ssh 로그인 설정
### ssh 서버 설치
```bash
$ sudo apt-get install openssh-server
# 22번 포트 허용
$ sudo ufw allow 22
```

### ssh key 생성
```bash
# 키가 없는 경우 키 생성
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa

# 키 생성 확인
$ ls ~/.ssh

# 퍼블릭 키를 authorized_keys에 추가. 비밀번호 없이 접속을 위함
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
$ chmod 0600 ~/.ssh/authorized_keys
```

### ssh localhost 접속 확인
```bash
$ ssh localhost
```

## 5. Hadoop 설치
### hadoop 다운로드
```bash
$ wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.2/hadoop-3.3.2.tar.gz
$ tar zxvf hadoop-3.3.2.tar.gz
```

### hadoop 명령어 PATH 등록  
~/.bashrc 파일
```bash
# hadoop
export HADOOP_HOME=/path/to/hadoop-3.3.2

# path
export PATH=$PATH:$HADOOP_HOME/bin
```

수정 내역 반영
```bash
$ source ~/.bashrc
```

hadoop 명령어 확인
```bash
$ hadoop version
```

### 설정 파일 수정
/path/to/hadoop/etc/hadoop/core-site.xml
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

네임노드 디렉토리와 데이터노드 디렉토리 생성
```bash
$ mkdir -p dfs/data
$ mkdir -p dfs/name
```

/path/to/hadoop/etc/hadoop/hdfs-site.xml
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/home/fastcampus/hadoop-3.3.2/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/home/fastcampus/hadoop-3.3.2/dfs/data</value>
    </property>
</configuration>
```

/path/to/hadoop/etc/hadoop/mapred-site.xml
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*:$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
    </property>
</configuration>
```

/path/to/hadoop/etc/hadoop/yarn-site.xml
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

/path/to/hadoop/etc/hadoop/hadoop-env.sh
```bash
# JAVA_HOME 추가
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

## 6. HDFS 실행
### 네임 노드 포맷
```bash
$ hdfs namenode -format
```

### hdfs 시작
```bash
$ sbin/start-dfs.sh
```

### hdfs web ui 접속
http://localhost:9870

## 7. YARN 실행
```bash
$ sbin/start-yarn.sh
```

### resource manage web ui 접속
http://localhost:8088/

## 8. 예제 실행
```bash
$ hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.2.jar pi 16 10000
```