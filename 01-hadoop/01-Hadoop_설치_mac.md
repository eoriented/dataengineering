# 하둡 설치 (mac)

## 1. 사전 준비
홈브류는 맥용 패키지 관리도구입니다.
### 1.1 HomeBrew 설치
```bash
$ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 1.2 Java 설치
jdk8 이상 버전으로 설치

```bash
$ brew search openjdk8
$ brew install adoptopenjdk8
```

~/.zshrc 파일
```bash
# JAVA_HOME 환경 변수 추가
export JAVA_HOME=$(/usr/libexec/java_home)
```

### 1.3 ssh 로그인 설정
ssh 명령어 확인 (ssh가 없으면 ssh를 설치해야 합니다)
```bash
$ ssh localhost
```

위 명령어로 접속이 안되는 경우 접속이 가능하도록 설정
```bash
# 키가 없는 경우 키 생성
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa

# 퍼블릭 키를 authorized_keys에 추가. 비밀번호 없이 접속을 위함
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
$ chmod 0600 ~/.ssh/authorized_keys
```

#### 참고
맥에서 접속이 안되는 경우 시스템 설정 확인 필요
- 시스템 설정 > 공유 > 원격 로그인 체크

## 2. Hadoop 다운로드
#### hadoop 다운로드
```bash
$ wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.2/hadoop-3.3.2.tar.gz
$ tar zxvf hadoop-3.3.2.tar.gz
$ cd hadoop-3.3.2/
$ vim etc/hadoop/hadoop-env.sh
```

#### hadoop 명령어 PATH 등록  
~/.zshrc 파일
```bash
# hadoop
export HADOOP_HOME=/path/to/hadoop-3.3.2

# path
export PATH=$PATH:$HADOOP_HOME/bin
```

수정 내역 반영
```bash
$ source ~/.zshrc
```

## 3. 설정 파일 수정
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
        <value>/path/to/hadoop-3.3.2/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/path/to/hadoop-3.3.2/dfs/data</value>
    </property>
</configuration>
```

etc/hadoop/mapred-site.xml
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

## 4. HDFS 실행
네임 노드 포맷
```bash
$ hdfs namenode -format
```

hdfs 시작
```bash
$ sbin/start-dfs.sh
```

## 5. hdfs web ui 접속
http://localhost:9870

## 6. yarn 실행
```bash
$ sbin/start-yarn.sh
```

## 7. resource manage web ui
http://localhost:8088/

## 8. 예제 실행
```bash
$ hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.2.jar pi 16 10000
```