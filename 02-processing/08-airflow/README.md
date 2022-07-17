# Apache airflow
## 1. Airflow 설치
### 1) 설치
```bash
$ pip3 install apache-airflow

# 버전 확인
$ airflow version

# 메타데이터 스토어 초기화
$ airflow db init

# 웹서버 실행
$ airflow webserver
```

### 2) Web ui 접속
http://localhost:8080

### 3) 사용자 추가
```bash 
$ airflow users create --username admin --password admin --firstname admin --lastname admin --role Admin --email admin@example.com
```

사용자 정보 확인
````bash
$ sqlite3 ~/airflow/airflow.db
````

```sqlite
.tables

SELECT * FROM ad_user;
```

### 4) scheduler 실행
```bash
$ airflow scheduler
```

### 5) dag 디렉토리 생성
```bash
$ mkdir -p ~/airflow/dag
```

## 2. BTC pipeline 실습
### 1) 사전 준비
MySql이 실행 중이어야 합니다.

Database 생성
```bash
mysql> create database airflow;
```

### 2) MySQL provider 설치
```bash
$ pip3 install apache-airflow-providers-mysql
```

### 3) PyMySql 설치
```bash
$ pip3 install PyMySQL
```

### 4) Airflow 시작
```bash
$ airflow webserver
$ airflow scheduler
```

### 5) Connection 추가
- Airflow web ui 접속
- 상단 Admin 메뉴 -> Connections 클릭
- mysql connection 생성
  - host : localhost
  - schema : airflow
  - Login : username
  - Password : password
- http connection 생성
  - host : https://api.coindesk.com/