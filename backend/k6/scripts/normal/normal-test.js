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
    normal_load: {
      executor: 'ramping-arrival-rate',
      startRate: 1,
      timeUnit: '1s',
      preAllocatedVUs: 50,
      maxVUs: 500,
      stages: [
        {duration: '1m', target: 150},  // 1분간 1 -> 150 iteration/s 증가
        {duration: '3m', target: 150},  // 3분간 150 iteration/s 유지
        {duration: '1m', target: 1},    // 1분간 150 -> 1 iteration/s 감소
      ],
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

// 1명의 VU가 3개 API를 순차적으로 호출
export default function () {
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  // 1. 조직 조회 API
  const orgResponse = http.get(`${BASE_URL}/organizations/${orgUuid}`, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    tags: {name: 'GetOrganization'},
  });

  const orgSuccess = check(orgResponse, {
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
  errorRateOrganization.add(!orgSuccess);

  // 2. 피드백 조회 API
  const size = 10;
  const sortBy = SORT_OPTIONS[Math.floor(Math.random() * SORT_OPTIONS.length)];
  const status = Math.random() > 0.5 ? PROCESS_STATUS[Math.floor(
      Math.random() * PROCESS_STATUS.length)] : '';
  const cursorId = Math.random() > 0.7 ? Math.floor(Math.random() * 4000000) + 1
      : '';

  let queryParams = `size=${size}&sortBy=${sortBy}`;
  if (status) {
    queryParams += `&status=${status}`;
  }
  if (cursorId) {
    queryParams += `&cursorId=${cursorId}`;
  }

  const feedbacksResponse = http.get(
      `${BASE_URL}/organizations/${orgUuid}/feedbacks?${queryParams}`, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        tags: {name: 'GetFeedbacks'},
      });

  const feedbacksSuccess = check(feedbacksResponse, {
    '[Feedbacks] status is 200': (r) => r.status === 200,
    '[Feedbacks] response time <= 500ms': (r) => r.timings.duration <= 500,
  });
  errorRateFeedbacks.add(!feedbacksSuccess);

  // 3. 통계 조회 API
  const statisticResponse = http.get(
      `${BASE_URL}/organizations/${orgUuid}/statistic`, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        tags: {name: 'GetStatistic'},
      });

  const statisticSuccess = check(statisticResponse, {
    '[Statistic] status is 200': (r) => r.status === 200,
    '[Statistic] response time <= 500ms': (r) => r.timings.duration <= 500,
  });
  errorRateStatistic.add(!statisticSuccess);
}