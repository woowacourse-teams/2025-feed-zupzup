import http from 'k6/http';
import {check, sleep} from 'k6';
import {ORGANIZATION_UUIDS} from '../../utils/common.js';
import {BASE_URL} from '../../utils/secret.js';

export const options = {
  stages: [
    {duration: '1m', target: 200},
    {duration: '4m', target: 200}
  ]
};

export default function () {
  // 랜덤 조직 UUID 선택
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  const url = `${BASE_URL}/organizations/${orgUuid}/statistic`;

  // API 요청
  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  });

  check(response, {
    'total requests': () => true,
    'status is 200': (r) => r.status === 200,
    'status is not 200': (r) => r.status !== 200,
    'response time > 500ms': (r) => r.timings.duration > 500,
    'response time <= 500ms': (r) => r.timings.duration <= 500,
  });

  sleep(0.5);
}
