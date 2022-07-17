from datetime import datetime
import json
from airflow import DAG
from airflow.providers.mysql.operators.mysql import MySqlOperator
from airflow.providers.http.sensors.http import HttpSensor
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.operators.python import PythonOperator
import pymysql


default_args = {
    'start_date': datetime(2022, 1, 1)
}


def insert_btc(**kwargs):
    ti = kwargs['ti']
    value = ti.xcom_pull(task_ids='get_btc_price', key='return_value')
    print(f'value: {value}')

    conn = pymysql.connect(host='localhost',
                           user='root',
                           password='fastcampus',
                           database='airflow',
                           cursorclass=pymysql.cursors.DictCursor)
    with conn:
        with conn.cursor() as cursor:
            sql = f"INSERT INTO `btc_price` (ts, usd_price) VALUES ('{value['time']['updatedISO']}', {value['bpi']['USD']['rate_float']})"
            print(f'sql: {sql}')
            cursor.execute(sql)
        conn.commit()


def notify_function(**kwargs):
    ti = kwargs['ti']
    value = ti.xcom_pull(task_ids='get_btc_price', key='return_value')
    threshold = 18000

    if value['bpi']['USD']['rate_float'] < threshold:
        print('Threshold notification')
    else:
        print('Do nothing')


with DAG(
    dag_id='btc_pipeline',
    schedule_interval='@daily',
    catchup=False,
    default_args=default_args
) as dag:
    create_btc_price_table = MySqlOperator(
        task_id='create_btc_price_table',
        mysql_conn_id='local_mysql',
        sql=r"""
        CREATE TABLE IF NOT EXISTS btc_price(
        id INT AUTO_INCREMENT,
        ts TIMESTAMP,
        usd_price DOUBLE,
        PRIMARY KEY (id, ts)
        );
        """
    )

    api_server_sensor = HttpSensor(
        task_id='api_server_sensor',
        http_conn_id='coindesk_api_server',
        endpoint='v1/bpi/currentprice.json'
    )

    get_btc_price = SimpleHttpOperator(
        task_id='get_btc_price',
        http_conn_id='coindesk_api_server',
        endpoint='v1/bpi/currentprice.json',
        method='GET',
        response_filter=lambda response: json.loads(response.text),
        log_response=True
    )

    insert_btc_price = PythonOperator(
        task_id='insert_btc_price',
        python_callable=insert_btc
    )

    notify_threshold = PythonOperator(
        task_id='notify_threshold',
        python_callable=notify_function
    )

    create_btc_price_table >> api_server_sensor >> get_btc_price >> [insert_btc_price, notify_threshold]
