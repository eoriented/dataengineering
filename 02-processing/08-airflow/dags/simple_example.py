from datetime import datetime
from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator
from airflow.operators.empty import EmptyOperator


def print_hello(val):
    print(f'hello {val}...')


default_args = {
    'start_date': datetime(2021, 10, 11)
}

with DAG(
    dag_id='simple_example',
    schedule_interval='5 4 * * *',
    catchup=False,
    default_args=default_args
) as dag:
    run_bash1 = BashOperator(
        task_id='run_bash',
        bash_command='echo hello fastcampus...'
    )

    run_bash2 = BashOperator(
        task_id='run_bash2',
        bash_command='echo hello bigdata...'
    )

    run_python1 = PythonOperator(
        task_id='run_python1',
        python_callable=print_hello,
        op_kwargs={'val': 'airflow'},
        dag=dag
    )

    run_last = EmptyOperator(
        task_id='run_last'
    )

    run_bash1 >> [run_bash2, run_python1] >> run_last