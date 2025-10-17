#!/bin/bash

# InfluxDB 환경 변수 설정
if [ -f .env ]; then
  export $(cat .env | xargs)
fi

K6_WEB_DASHBOARD=true k6 run \
  --out influxdb=${INFLUXDB_URL} \
  -e K6_INFLUXDB_DB=${INFLUXDB_DB} \
  -e K6_INFLUXDB_USERNAME=${INFLUXDB_USERNAME} \
  -e K6_INFLUXDB_PASSWORD=${INFLUXDB_PASSWORD} \
  scripts/spike/spike-test.js
