# Trino

## 1. 실습 준비
- Docker desktop 실행

## 2. Trino 기본 실습
### 1) 서버 실행
```bash
$ docker run -p 8080:8080 --name trino trinodb/trino
```

### 2) web ui 접속
http://localhost:8080
username: 아무거나

### 3) Trino CLI
```bash
$ docker exec -it trino trino
```

### 4) 실습
```bash
# 카탈로그 확인
trino> show catalogs;

# 스키마 확인
trino> show schemas in tpch;

# 테이블 확인
trino> show tables in tpch.sf1;

# 사용할 스키마 선택
trino> use tpch.sf1;

# 테이블 정보 확인
trino:sf1> desc orders;
```

```sql
select * from customer limit 10;
```

## 3. Trino hive connector 실습
### 0) 기존의 trino docker는 삭제
```bash
$ docker rm trino
```

### 1) docker-compose 실행
```bash
# dataengineering/03-processing/02-trino 이동
$ docker-compose up
```

### 2) beeline 접속
```bash
$ docker exec -it hadoop-hive beeline
beeline> !connect jdbc:hive2://localhost:10000
# username: scott
# password: tiger

0: jdbc:hive2://localhost:10000> show schemas;
0: jdbc:hive2://localhost:10000> CREATE SCHEMA fastcampus;
```

### 3) Trino CLI 접속
```bash
$ docker exec -it trino trino
trino> show schemas in hdfs;
# hive에서 추가한 스키마 확인
```

### 4) Hive에 테이블 생성 및 데이터 추가
테이블 생성
```hiveql
CREATE TABLE employee (name STRING, age INT, department STRING);
```

데이터 추가
```hiveql
INSERT INTO TABLE employee VALUES ('John', 30, 'Marketing'), ('Bill', 32, 'Sales'), ('Olivia', 28, 'Human Resource');
```

데이터 조회
```hiveql
SELECT * FROM employee;
```

### 5) Trino에서 Hive 테이블 조회
스키마 사용 설정
```sql
use hdfs.fastcampus
```

테이블 조회
```sql
show tables;
SELECT * FROM employee;
```

## 참고
- <container> exited with code 137
  - docker의 메모리가 부족한 것이니 docker desktop의 설정에서 resource의 메모리 양을 증가 시켜줍니다.