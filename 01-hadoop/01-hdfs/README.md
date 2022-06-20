# HDFS Java API 실습

## 0. 사전 준비
### 1) Hadoop cluster
하둡 클러스터가 동작 중이어야 합니다.

### 2) Maven 설치
#### Maven 다운로드
- [maven 홈페이지](https://maven.apache.org/download.cgi) 접속
- Binary tar.gz archive 다운로드
- 혹은 아래의 커맨드를 통해 다운로드

```bash
$ wget https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz
$ tar zxvf apache-maven-3.8.5-bin.tar.gz
```

#### PATH 환경 변수 등록
~/.zshrc
```bash
export MAVEN_HOME=/path/to/apache-maven-3.8.5
export PATH=$PATH:$MAVEN_HOME/bin
```

## 1. 빌드 방법
```bash
$ mvn package
```

## 2. 실행하기
### FileSystemPrint
HDFS에 존재하는 파일을 stdout으로 출력하기

#### 파일 업로드
```bash
$ hadoop fs -put /path/to/hdfs-example/pom.xml /user/fastcampus
$ hadoop fs -ls /user/fastcampus
```

```bash
# hdfs://localhost:9000/와 같이 스킴 지정가능
hadoop jar target/hdfs-1.0-SNAPSHOT.jar com.fastcampus.hadoop.FileSystemPrint /user/fastcampus/input/LICENSE.txt
```

### ListStatus
디렉토리 파일 목록 조회

```bash
hadoop jar target/hdfs-1.0-SNAPSHOT.jar com.fastcampus.hadoop.ListStatus /user/fastcampus/
```

### CopyFromLocal
로컬 파일을 HDFS에 복사하기

```bash
hadoop jar target/hdfs-1.0-SNAPSHOT.jar com.fastcampus.hadoop.CopyFromLocal ./pom.xml /user/fastcampus/input/pom.xml
hadoop fs -ls /user/fastcampus/input
```

### DeleteFile
파일 삭제하기

```bash
hadoop jar target/hdfs-1.0-SNAPSHOT.jar com.fastcampus.hadoop.DeleteFile /user/fastcampus/input/pom.xml
hadoop fs -ls /user/fastcampus/input
```