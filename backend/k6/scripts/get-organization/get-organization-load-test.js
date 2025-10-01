import http from 'k6/http';
import {check, sleep} from 'k6';
import {Rate} from 'k6/metrics';
import {
  ORGANIZATION_UUIDS,
} from "../../utils/common.js";
import {BASE_URL} from "../../utils/secret.js";

export const errorRate = new Rate('errors');

export const options = {
  stages: [
    {duration: '1m', target: 200},
    {duration: '4m', target: 200}
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내
    http_req_failed: ['rate<0.1'],     // 실패율 10% 미만
    errors: ['rate<0.1'],              // 에러율 10% 미만
  },
};

/////////////////////////////////////

export default function () {
  // 랜덤 조직 UUID 선택
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  const url = `${BASE_URL}/organizations/${orgUuid}`;


  // API 요청
  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  });

  // 응답 검증
  const isSuccess = check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
    'response has success field': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.hasOwnProperty('success');
      } catch {
        return false;
      }
    },
    'response success is true': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch {
        return false;
      }
    },
    'response has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data &&
            body.data.hasOwnProperty('organizationName') &&
            body.data.hasOwnProperty('categories');
      } catch {
        return false;
      }
    },
  });

  // 에러율 기록
  errorRate.add(!isSuccess);

  // 요청 간격 (1초)
  sleep(1);
}


