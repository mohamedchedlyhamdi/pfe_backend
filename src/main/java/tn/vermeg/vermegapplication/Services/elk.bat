@echo off
REM Set paths to your ELK installation directories
SET ELASTICSEARCH_PATH=C:\Users\mchamdi_tr\Desktop\ELK\elasticsearch-8.12.2-windows-x86_64 (1)\elasticsearch-8.12.2\bin
SET LOGSTASH_PATH=C:\Users\mchamdi_tr\Desktop\ELK\logstash-8.12.2\bin
SET KIBANA_PATH=C:\Users\mchamdi_tr\Desktop\ELK\kibana-8.12.2-windows-x86_64\kibana-8.12.2\bin

REM Start Elasticsearch
start "" "%ELASTICSEARCH_PATH%\elasticsearch.bat"

REM Wait for Elasticsearch to fully start
timeout /t 60

REM Start Logstash
start "" "%LOGSTASH_PATH%\logstash.bat"

REM Start Kibana
start "" "%KIBANA_PATH%\kibana.bat"
