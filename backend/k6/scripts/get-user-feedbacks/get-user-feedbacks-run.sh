#!/bin/bash

# InfluxDB 환경 변수 설정
source <(node -e "
const { INFLUXDB_URL, INFLUXDB_DB, INFLUXDB_USERNAME, INFLUXDB_PASSWORD } = require('./utils/secret.js');
console.log(\`export INFLUXDB_URL=\${INFLUXDB_URL}\`);
console.log(\`export INFLUXDB_DB=\${INFLUXDB_DB}\`);
console.log(\`export INFLUXDB_USERNAME=\${INFLUXDB_USERNAME}\`);
console.log(\`export INFLUXDB_PASSWORD=\${INFLUXDB_PASSWORD}\`);
")

K6_WEB_DASHBOARD=true k6 run \
  --out influxdb=${INFLUXDB_URL} \
  -e K6_INFLUXDB_DB=${INFLUXDB_DB} \
  -e K6_INFLUXDB_USERNAME=${INFLUXDB_USERNAME} \
  -e K6_INFLUXDB_PASSWORD=${INFLUXDB_PASSWORD} \
  scripts/get-user-feedbacks/get-user-feedbacks-load-test.js
