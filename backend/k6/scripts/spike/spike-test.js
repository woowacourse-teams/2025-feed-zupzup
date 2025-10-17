import http from 'k6/http';
import {check, sleep} from 'k6';
import {Rate} from 'k6/metrics';
import {
  ORGANIZATION_UUIDS,
  PROCESS_STATUS,
  SORT_OPTIONS
} from '../../utils/common.js';
import {BASE_URL} from '../../utils/secret.js';

// 각 API별 에러율 메트릭
export const errorRateOrganization = new Rate('errors_organization');
export const errorRateFeedbacks = new Rate('errors_feedbacks');
export const errorRateStatistic = new Rate('errors_statistic');

export const options = {
  scenarios: {
    // 조직 조회 테스트 - 300 RPS, 스파이크 테스트
    get_organization: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        {duration: '1m', target: 150},  // 1분간 1 -> 150 VU 증가
        {duration: '1m', target: 150},  // 1분간 150 VU 유지
        {duration: '10s', target: 500}, // 10초간 150 -> 500 VU 급증 (스파이크)
        {duration: '30s', target: 500}, // 30초간 500 VU 유지
        {duration: '1m', target: 150},  // 1분간 500 -> 150 VU 감소
        {duration: '5m', target: 150},  // 5분간 150 VU 유지
      ],
      exec: 'getOrganization',
    },
    // 피드백 조회 테스트 - 300 RPS, 스파이크 테스트
    get_feedbacks: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        {duration: '1m', target: 150},
        {duration: '1m', target: 150},
        {duration: '10s', target: 500},
        {duration: '30s', target: 500},
        {duration: '1m', target: 150},
        {duration: '5m', target: 150},
      ],
      exec: 'getFeedbacks',
    },
    // 통계 조회 테스트 - 300 RPS, 스파이크 테스트
    get_statistic: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        {duration: '1m', target: 150},
        {duration: '1m', target: 150},
        {duration: '10s', target: 500},
        {duration: '30s', target: 500},
        {duration: '1m', target: 150},
        {duration: '5m', target: 150},
      ],
      exec: 'getStatistic',
    },
  },
  thresholds: {
    // 전체 응답 시간
    http_req_duration: ['p(95)<2000', 'p(99)<3000'],
    // 각 API별 에러율
    errors_organization: ['rate<0.1'],
    errors_feedbacks: ['rate<0.1'],
    errors_statistic: ['rate<0.1'],
    // 전체 실패율
    http_req_failed: ['rate<0.1'],
  },
};

// 조직 조회 API
export function getOrganization() {
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  const url = `${BASE_URL}/organizations/${orgUuid}`;

  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    tags: {name: 'GetOrganization'},
  });

  const isSuccess = check(response, {
    '[Organization] status is 200': (r) => r.status === 200,
    '[Organization] response time < 500ms': (r) => r.timings.duration < 500,
    '[Organization] has valid data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true &&
            body.data &&
            body.data.hasOwnProperty('organizationName') &&
            body.data.hasOwnProperty('categories');
      } catch {
        return false;
      }
    },
  });

  errorRateOrganization.add(!isSuccess);
  sleep(0.1);
}

// 피드백 조회 API
export function getFeedbacks() {
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

  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    tags: {name: 'GetFeedbacks'},
  });

  const isSuccess = check(response, {
    '[Feedbacks] status is 200': (r) => r.status === 200,
    '[Feedbacks] response time <= 500ms': (r) => r.timings.duration <= 500,
  });

  errorRateFeedbacks.add(!isSuccess);
  sleep(0.1);
}

// 통계 조회 API
export function getStatistic() {
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  const url = `${BASE_URL}/organizations/${orgUuid}/statistic`;

  const response = http.get(url, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    tags: {name: 'GetStatistic'},
  });

  const isSuccess = check(response, {
    '[Statistic] status is 200': (r) => r.status === 200,
    '[Statistic] response time <= 500ms': (r) => r.timings.duration <= 500,
  });

  errorRateStatistic.add(!isSuccess);
  sleep(0.1);
}
