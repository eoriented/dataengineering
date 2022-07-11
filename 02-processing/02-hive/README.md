# Apache Hive

## 1. Hive 설치
### 1) Hive download
```bash
$ wget https://dlcdn.apache.org/hive/hive-3.1.3/apache-hive-3.1.3-bin.tar.gz
$ tar zxvf apache-hive-3.1.3-bin.tar.gz
```

### 2) 환경 변수 추가
#### Mac OS
`.zshrc` 수정
```bash
# 추가
export HIVE_HOME=/path/to/apache-hive-3.1.3-bin

# 수정
export PATH=$PATH:$HIVE_HOME/bin
```

### 3) Hadoop cluster 실행
```bash
$ $HADOOP_HOME/sbin/start-all.sh
```

### 4) HDFS에 Hive에서 사용할 디렉토리 생성
```bash
# Hive의 중간 데이터 결과를 저장하기 위한 /tmp 디렉토리 생성
$ hadoop fs -mkdir /tmp
$ hadoop fs -chmod g+w /tmp
# hive.metastore.warehouse.dir의 기본 설정 위치. 데이터 웨어하우스를 저장하는 기본 디렉토리
$ hadoop fs -mkdir -p /user/hive/warehouse
$ hadoop fs -chmod g+w /user/hive/warehouse
```

### 4) Derby database 실행
```bash
$ $HIVE_HOME/bin/schematool -dbType derby -initSchema
```

### 5) Hive cli 실행
```bash
$ hive
```

## 2. Hive 실습
### 0) 사전 준비
#### 데이터 준비
dataset/employees.txt
```bash
# 샘플 : 10001,1953-09-02,Georgi,Facello,M,1986-06-26,d005
$ head dataset/employees
```

dataset/departments.txt
```bash
# 샘플 : d009,Customer Service
$ head dataset/departments
```

### 1) Hive Server 2 실행
```bash
$ hiveserver2 --hiveconf hive.server2.thrift.port=10000 --hiveconf hive.root.logger=DEBUG,console
```

### 2) beeline 실행
beeline : Hive server2에 접속하여 쿼리를 실행하기 위한 CLI 도구
```bash
$ beeline
beeline> !help
beeline> !connect jdbc:hive2://localhost:10000
# default user : scott
# default password : tiger
```

에러 발생
```
User: is not allowed to impersonate
```

hive-site.xml 생성 후
```bash
vim $HIVE_HOME/conf/hive-site.xml
```

아래의 설정 추가
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>hive.server2.enable.doAs</name>
        <value>false</value>
    </property>
</configuration>
```

hiveserver2 실행
```bash
$ hiveserver2 --hiveconf hive.server2.thrift.port=10000 --hiveconf hive.root.logger=DEBUG,console
```

beeline 실행
```bash
$ beeline
beeline> !connect jdbc:hive2://localhost:10000
# default user : scott
# default password : tiger
```

### 3) 테이블 생성

#### Managed table
employees 테이블 생성
```hiveql
CREATE TABLE employees
(
    emp_no     INT,
    birth_date DATE,
    first_name STRING,
    last_name  STRING,
    gender     STRING,
    hire_date  DATE,
    dept_no    STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n';
```

테이블 데이터 로드
```hiveql
-- path는 수정 필요
LOAD DATA LOCAL INPATH '/path/to/dataengineering/dataset/employees'
OVERWRITE INTO TABLE employees;
```

데이터 조회
```hiveql
SELECT * FROM employees LIMIT 10;

-- 생일이 같은 사람 수를 많은 수로 정렬
SELECT birth_date, count(birth_date) AS count
FROM employees
GROUP BY birth_date
ORDER BY count DESC
LIMIT 10;
```

테이블 정보 확인
```bash
0: jdbc:hive2://localhost:10000> DESCRIBE EXTENDED employees;
```

저장된 파일 위치 확인
```bash
# employees 디렉토리 존재
$ hadoop fs -ls /user/hive/warehouse
```

테이블 삭제
```bash
0: jdbc:hive2://localhost:10000> DROP TABLE employees;
```

삭제 후 다시 파일 위치 확인
```bash
# employees가 존재하지 않음
$ hadoop fs -ls /user/hive/warehouse
```

#### External Table
Dataset 확인
```bash
$ hadoop fs -ls /user/fastcampus/dataset

# 파일이 존재하지 않으면 업로드
$ hadoop fs -mkdir -p /user/fastcampus/hive/dataset/employees
$ hadoop fs -copyFromLocal ./dataset/employees /user/fastcampus/hive/dataset/employees
```

External table 생성
```hiveql
CREATE EXTERNAL TABLE employees
(
    emp_no     INT,
    birth_date DATE,
    first_name STRING,
    last_name  STRING,
    gender     STRING,
    hire_date  DATE,
    dept_no    STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '/user/fastcampus/hive/dataset/employees';
```

데이터 확인
```hiveql
SELECT * FROM employees
LIMIT 100
```

테이블 삭제
```
DROP TABLE employees;
show tables;
```

HDFS에 데이터가 그대로 있는지 확인
```bash
$ hadoop fs -ls /user/fastcampus/dataset/employees
```

### 4) Join 실습
#### Hdfs에 데이터 업로드
```bash
# 이전에 업로드 하지 않았다면 업로드
$ hadoop fs -mkdir -p /user/fastcampus/hive/dataset/employees
$ hadoop fs -copyFromLocal ./dataset/employees /user/fastcampus/hive/dataset/employees

# departments 데이터 업로드
$ hadoop fs -mkdir -p /user/fastcampus/hive/dataset/departments
$ hadoop fs -copyFromLocal ./dataset/departments /user/fastcampus/hive/dataset/departments
```

#### 테이블 생성
employees 테이블 생성
```hiveql
CREATE EXTERNAL TABLE employees
(
    emp_no     INT,
    birth_date DATE,
    first_name STRING,
    last_name  STRING,
    gender     STRING,
    hire_date  DATE,
    dept_no    STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '/user/fastcampus/hive/dataset/employees';
```

departments 테이블 생성
```hiveql
CREATE EXTERNAL TABLE departments
(
    dept_no STRING,
    dept_name STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '/user/fastcampus/hive/dataset/departments';
```

#### Join 실행
beeline에서 join 실행
```
SELECT 
    e.emp_no,
    e.first_name,
    e.last_name,
    d.dept_name
FROM employees e
JOIN departments d ON e.dept_no = d.dept_no
LIMIT 10;
```