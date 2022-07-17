# Spark Streaming

## 1. NetworkWordCount 실습
### 1) netcat 실행
```
$ nc -lk 9999
```

### 2) wordcount.py 실행
```
$ spark-submit wordcount.py
```

## 2. WindowWordCount 실습
### 1) netcat 실행 
```
$ nc -lk 9999
```

### 2) window_wordcount.py 실행
```
$ spark-submit window_wordcount.py
```

## 3. StructuredNetworkWordCount 실습
### 1) netcat 실행
```
$ nc -lk 9999
```

### 2) structured_wordcount.py 실행
```
$ spark-submit window_wordcount.py
```

## 4. StructuredWindowWordCount 실습
### 1) netcat 실행
```
$ nc -lk 9999
```

### 2) structured_window_wordcount.py 실행
```
$ spark-submit structured_window_wordcount.py
```

## 5. StructuredWindowWordCountSql 실습
### 1) netcat 실행
```
$ nc -lk 9999
```

### 2) structured_window_wordcount_sql.py 실행
```
$ spark-submit structured_window_wordcount_sql.py
```