import http from 'k6/http';
import {check, sleep} from 'k6';
import {
  ORGANIZATION_UUIDS,
  PROCESS_STATUS,
  SORT_OPTIONS
} from '../../utils/common.js';
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

  // 랜덤 파라미터 생성
  const size = 10;
  const sortBy = SORT_OPTIONS[Math.floor(Math.random() * SORT_OPTIONS.length)];
  const status = Math.random() > 0.5 ? PROCESS_STATUS[Math.floor(
      Math.random() * PROCESS_STATUS.length)] : '';
  const cursorId = Math.random() > 0.7 ? Math.floor(Math.random() * 4000000) + 1
      : '';

  // 쿼리 파라미터 구성
  let queryParams = `size=${size}&sortBy=${sortBy}`;
  if (status) {
    queryParams += `&status=${status}`;
  }
  if (cursorId) {
    queryParams += `&cursorId=${cursorId}`;
  }

  const url = `${BASE_URL}/organizations/${orgUuid}/feedbacks?${queryParams}`;

  // API 요청
  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  });

  check(response, {
    'status is 200': (r) => r.status === 200,
    'status is not 200': (r) => r.status !== 200,
    'response time > 500ms': (r) => r.timings.duration > 500,
    'response time <= 500ms': (r) => r.timings.duration <= 500,
  });

  sleep(0.5);
}
