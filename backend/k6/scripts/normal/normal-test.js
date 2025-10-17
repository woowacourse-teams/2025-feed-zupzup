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

export const options = {
  scenarios: {
    normal_load: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        {duration: '1m', target: 150},  // 1분간 1 -> 150 VU 증가
        {duration: '3m', target: 150},  // 3분간 150 VU 유지
        {duration: '1m', target: 1},    // 1분간 150 -> 1 VU 감소
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

// 1명의 VU가 3개 API를 동시에 호출 (실제 페이지 로딩 시뮬레이션)
export default function () {
  const orgUuid = ORGANIZATION_UUIDS[Math.floor(
      Math.random() * ORGANIZATION_UUIDS.length)];

  // 피드백 쿼리 파라미터 생성
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

  // 3개 API를 동시에 호출 (batch)
  const responses = http.batch([
    ['GET', `${BASE_URL}/organizations/${orgUuid}`, null, {
      headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      tags: {name: 'GetOrganization'},
    }],
    ['GET', `${BASE_URL}/organizations/${orgUuid}/feedbacks?${queryParams}`, null, {
      headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      tags: {name: 'GetFeedbacks'},
    }],
    ['GET', `${BASE_URL}/organizations/${orgUuid}/statistic`, null, {
      headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      tags: {name: 'GetStatistic'},
    }],
  ]);

  // 각 응답 검증
  const orgSuccess = check(responses[0], {
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

  const feedbacksSuccess = check(responses[1], {
    '[Feedbacks] status is 200': (r) => r.status === 200,
    '[Feedbacks] response time <= 500ms': (r) => r.timings.duration <= 500,
  });
  errorRateFeedbacks.add(!feedbacksSuccess);

  const statisticSuccess = check(responses[2], {
    '[Statistic] status is 200': (r) => r.status === 200,
    '[Statistic] response time <= 500ms': (r) => r.timings.duration <= 500,
  });
  errorRateStatistic.add(!statisticSuccess);
}
