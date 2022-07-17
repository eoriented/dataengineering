# HBase
## 1. HBase 설치
```bash
$ wget https://dlcdn.apache.org/hbase/2.4.12/hbase-2.4.12-bin.tar.gz
$ tar zxvf hbase-2.4.12-bin.tar.gz
```

## 2. HBase 시작하기
$HBASE_HOME/conf/hbase-site.xml 수정
```xml
<property>
  <name>hbase.rootdir</name>
  <value>file:///path/to/hbase/rootdir</value>
</property>
```

```bash
# HBase 시작하기
$ ./bin/start-hbase.sh
```

- master ui 접속
  - http://localhost:16010/

## 3. HBase Shell 실습
```bash
# HBase shell 실행
$ ./bin/hbase shell
```

### 1) Shell 도움말
```
hbase:001:0> help
```

### 2) 테이블 생성
```
hbase:002:0> create 'test', 'cf'
```

### 3) 테이블 목록 확인
```
hbase:003:0> list
hbase:004:0> list 'test'
```

### 4) 테이블 상세 정보 확인
```
hbase:005:0> describe 'test'
```

### 5) 데이터 넣기
```
hbase:006:0> put 'test', 'row1', 'cf:a', 'value1'
hbase:007:0> put 'test', 'row2', 'cf:b', 'value2'
hbase:008:0> put 'test', 'row3', 'cf:c', 'value3'
```

### 6) 테이블 스캔하기
```
hbase:009:0> scan 'test'
```

### 7) 데이터 가져오기
```
hbase:010:0> get 'test', 'row1'
```

### 8) 테이블 disable, enable 하기
```
hbase:011:0> disable 'test'
hbase:012:0> get 'test', 'row1'

hbase:013:0> enable 'test'
hbase:014:0> get 'test', 'row1'
```

### 9) 테이블 삭제하기
```
hbase:015:0> disable 'test'
hbase:016:0> drop 'test'
```

### 10) Shell 종료하기
```
hbase:017:0> quit (or exit)
```

## 4. HBase Java API
### 1) 테이블 생성 예제
hbase-example/src/main/java/com/fastcampus/hbase/CreateTable.java

### 2) 테이블 목록 예제
hbase-example/src/main/java/com/fastcampus/hbase/ListTable.java

### 3) 테이블 상세 정보 확인 예제
hbase-example/src/main/java/com/fastcampus/hbase/DescriptionTable.java

### 4) 데이터 넣기 예제
hbase-example/src/main/java/com/fastcampus/hbase/PutExample.java

### 5) 데이터 스캔하기 예제
hbase-example/src/main/java/com/fastcampus/hbase/ScanExample.java

### 6) 데이터 가져오기 예제
hbase-example/src/main/java/com/fastcampus/hbase/GetExample.java

### ) 테이블 disable 및 삭제하기 예제
hbase-example/src/main/java/com/fastcampus/hbase/DisableAndDropTable.java
