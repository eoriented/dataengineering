# Apache Sqoop

## 1. 사전 준비
### 1) Hadoop Cluster
Hadoop cluster가 동작 중이어야 합니다.

### 2) MySql 설치
my sql 설치
```
$ brew search mysql
$ brew install mysql
```

mysql 서비스 시작
```
$ brew services start mysql
```

mysql 초기 설정
```
$ mysql_secure_installation
```

mysql 접속
```
$ mysql -u root -p
```

### 3) mysql 실습용 데이터베이스 구성
  - [참고 문서](https://dev.mysql.com/doc/employee/en/employees-installation.html)
  - [github 페이지](https://github.com/datacharmer/test_db) 방문
  - Release 페이지로 접속하여 [파일 다운로드](https://github.com/datacharmer/test_db/releases/download/v1.0.7/test_db-1.0.7.tar.gz)

파일 압축 해제
```
$ tar zxvf test_db-1.0.7.tar.gz
```

employees 데이터베이스 넣기
```
$ cd test_db
$ mysql -u root -p < employees.sql
```

데이터 확인을 위해 mysql 접속
```
$ mysql -u root -p
```

데이터 확인
```mysql
show databases;
use employees;
show tables;
select * from employees limit 10;
select * from departments limit 10;
select count(*) from departments;
```

## 2. Sqoop 설치
### 1) Sqoop download
```
$ wget http://archive.apache.org/dist/sqoop/1.4.7/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz
$ tar zxvf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz
```

### 2) PATH 추가
```
$ vim ~/.zshrc
```

```
export SQOOP_HOME=/path/to/sqoop
PATH=$PATH:$SQOOP_HOME/bin
```

### 3) mysql connector 설치
- [mysql connector 다운로드 사이트](https://dev.mysql.com/downloads/connector/j/)
- Platform Independent 선택
- TAR Archive 다운로드
- TAR 압축 해제
```
$ tar zxvf mysql-connector-java-8.0.28.tar.gz
```

- `mysql-connector-java-8.0.28.jar` 파일을 $SQOOP_HOME/lib으로 이동

### 4) import 실행
```bash
$ bin/sqoop import --connect jdbc:mysql://localhost/employees --username root --password your_password --query 'SELECT e.emp_no, e.birth_date, e.first_name, e.last_name, e.gender, e.hire_date, d.dept_no FROM employees e, dept_emp d WHERE (e.emp_no = d.emp_no) AND $CONDITIONS' --target-dir /user/fastcampus/sqoop/employees --split-by e.emp_no
```

### 5) 에러 문제 해결
```
Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/commons/lang/StringUtils
	at org.apache.sqoop.tool.BaseSqoopTool.validateHiveOptions(BaseSqoopTool.java:1583)
	at org.apache.sqoop.tool.ImportTool.validateOptions(ImportTool.java:1178)
	at org.apache.sqoop.Sqoop.run(Sqoop.java:137)
	at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:81)
	at org.apache.sqoop.Sqoop.runSqoop(Sqoop.java:183)
	at org.apache.sqoop.Sqoop.runTool(Sqoop.java:234)
	at org.apache.sqoop.Sqoop.runTool(Sqoop.java:243)
	at org.apache.sqoop.Sqoop.main(Sqoop.java:252)
Caused by: java.lang.ClassNotFoundException: org.apache.commons.lang.StringUtils
	at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
	... 8 more
```

- apache-common-lang2을 다운
```bash
$ wget https://dlcdn.apache.org//commons/lang/binaries/commons-lang-2.6-bin.tar.gz
$ tar zxvf commons-lang-2.6-bin.tar.gz
$ mv commons-lang-2.6.jar $SCOOP_HOME/lib
```

## 3. Sqoop CLI 실습
### 1) help
도움말 보기
```bash
$ bin/sqoop help
```

### 2) list-databases
데이터베이스의 database 스키마 목록 가져오기
```bash
$ bin/sqoop list-databases --connect jdbc:mysql://localhost --username <your_username> --password <your_password>
```

### 3) list-tables
데이터베이스의 스키마 내의 테이블 정보 가져오기
```bash
$ bin/sqoop list-tables --connect jdbc:mysql://localhost/<schema> --username <your_username> --password <your_password>
```

### 4) eval
데이터베이스에 쿼리 실행
```bash
$ bin/sqoop eval --connect jdbc:mysql://localhost/<schema> --username <your_username> --password <your_password> --query "select * from employees limit 10"
```

### 5) import
HDFS 디렉터리 생성
```bash
$ hadoop fs -mkdir -p /user/fastcampus/employees
```

데이터베이스의 테이블 데이터 가져오기 
```bash
$ bin/sqoop import --connect jdbc:mysql://localhost/employees --username <your_username> --password <your_password> --table employees --target-dir /user/fastcampus/employees1
$ hadoop fs -cat /user/fastcampus/employees
```

데이터베이스의 테이블 데이터 쿼리를 통해 가져오기
```bash
$ bin/sqoop import --connect jdbc:mysql://localhost/employees --username <your_username> --password <your_password> --query 'SELECT e.emp_no, e.birth_date, e.first_name, e.last_name, e.gender, e.hire_date FROM employees e WHERE $CONDITIONS' -m 1 --target-dir /user/fastcampus/employees2
$ hadoop fs -cat /user/fastcampus/employees2
```

병렬로 실행하여 가져오기
```bash
$ bin/sqoop import --connect jdbc:mysql://localhost/employees --username <your_username> --password <your_password> --query 'SELECT e.emp_no, e.birth_date, e.first_name, e.last_name, e.gender, e.hire_date FROM employees e WHERE $CONDITIONS' -m 3 --target-dir /user/fastcampus/employees2 --split-by emp_no
$ hadoop fs -ls /user/fastcampus/employees3
```

### 6) export
내보낼 데이터베이스 테이블 생성
```mysql
CREATE TABLE `export_employees`
(
    `emp_no`     int            NOT NULL,
    `birth_date` date           NOT NULL,
    `first_name` varchar(14)    NOT NULL,
    `last_name`  varchar(16)    NOT NULL,
    `gender`     enum ('M','F') NOT NULL,
    `hire_date`  date           NOT NULL,
    PRIMARY KEY (`emp_no`)
);
```

sqoop을 통해 데이터베이스로 내보내기
```bash
$ bin/sqoop export --connect jdbc:mysql://localhost/employees --username <your_username> --password <your_password> --table export_employees --export-dir /user/fastcampus/employees
```
