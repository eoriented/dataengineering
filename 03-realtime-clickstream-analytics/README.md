# 실시간 클릭 스트림 분석 프로젝트
## 1. 사전 준비
- Docker
- Kafka
- Flink
- MySQL
- Grafana

### 1) Kafka
- Kafka 실행
  ```bash
  $ docker-compose up
  ```
- Topic 생성
  - topic name : weblog
  - number of partitions : 3
  - replication factor : 1

### 2) MySQL
- MySQL 실행
- Database 생성
  ```sql
  CREATE DATABASE IF NOT EXISTS clickstream;
  ```
- 테이블 생성
  ```sql
  CREATE TABLE IF NOT EXISTS stats (
    ts TIMESTAMP NOT NULL,
    active_session INT,
    ads_per_second INT,
    request_per_second INT,
    error_per_second INT,
    PRIMARY KEY(ts)
  );
  ```

### 3) Grafana
- Grafana 실행
  ```bash
  $ docker run -d -p 3000:3000 grafana/grafana
  ```
- DataSource 추가
  - Configuration > Data sources > Add data source > MySQL
  ```
  - Host : host.docker.internal
  - Database : clickstream
  - User : ${username}
  - Password : ${password}
  ```
- Grafana 접속 확인
  - http://localhost:3000
  - 초기 비밀번호
    - username : admin
    - password : admin

## 2. Log Generator
- [log-generator project](./log-generator/)

## 3. Clickstream Analyzer
- [clickstream-analyzer project](./clickstream-analyzer/)

## 4. Grafana Dashboard 구성
- New Dashboard
- New Panel
  - Data source : MySQL
  - Query
  ```sql
  SELECT
    unix_timestamp(ts) AS "time",
    active_session
  FROM stats
  ORDER BY ts
  ```
    ```sql
  SELECT
    unix_timestamp(ts) AS "time",
    ads_per_second
  FROM stats
  ORDER BY ts
  ```
  ```sql
  SELECT
    unix_timestamp(ts) AS "time",
    request_per_second
  FROM stats
  ORDER BY ts
  ```
  ```sql
  SELECT
    unix_timestamp(ts) AS "time",
    error_per_second
  FROM stats
  ORDER BY ts
  ```