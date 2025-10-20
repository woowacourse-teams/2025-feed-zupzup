import http from 'k6/http';
import {check, sleep} from 'k6';
import {Trend} from 'k6/metrics';
import {
  ORGANIZATION_UUIDS,
  PROCESS_STATUS,
  SORT_OPTIONS
} from '../../utils/common.js';
import {BASE_URL} from '../../utils/secret.js';

// 각 API별 에러율 메트릭 (Rate)
// export const errorRateOrganization = new Rate('errors_organization');
// export const errorRateFeedbacks = new Rate('errors_feedbacks');
// export const errorRateStatistic = new Rate('errors_statistic');

// 전체 요청(3개 API batch) 응답시간 메트릭
export const batchResponseTime = new Trend('batch_response_time');

export const options = {
  scenarios: {
    spike_load: {
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
    },
  },
  thresholds: {
    // 전체 응답 시간
    http_req_duration: ['p(95)<2000', 'p(99)<3000'],
    // 각 API별 에러율
    // errors_organization: ['rate<0.1'],
    // errors_feedbacks: ['rate<0.1'],
    // errors_statistic: ['rate<0.1'],
    // 전체 실패율
    http_req_failed: ['rate<0.1'],
  },
};

// 1명의 VU가 3개 API를 동시에 호출 (실제 페이지 로딩 시뮬레이션)
export default function () {
  const orgUuid = 'b624e7f3-1993-41df-975f-eb48448e18f0';

  // 피드백 쿼리 파라미터 생성
  const size = 10;
  const sortBy = SORT_OPTIONS[Math.floor(Math.random() * SORT_OPTIONS.length)];
  const status = Math.random() > 0.5 ? PROCESS_STATUS[Math.floor(
      Math.random() * PROCESS_STATUS.length)] : '';
  const cursorId = Math.random() > 0.7 ? Math.floor(Math.random() * 633) + 1
      : '';

  let queryParams = `size=${size}&sortBy=${sortBy}`;
  if (status) {
    queryParams += `&status=${status}`;
  }
  if (cursorId) {
    queryParams += `&cursorId=${cursorId}`;
  }

  // 시작 시간 기록
  const startTime = new Date().getTime();

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
  check(responses[0], {
    '[Organization] status is 200': (r) => r.status === 200,
    '[Organization] response time < 500ms': (r) => r.timings.duration < 500,
    '[Organization] has valid data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.status === 200 &&
            body.data &&
            body.data.hasOwnProperty('organizationName') &&
            body.data.hasOwnProperty('categories');
      } catch {
        return false;
      }
    },
  });
  // errorRateOrganization.add(!orgSuccess);

  check(responses[1], {
    '[Feedbacks] status is 200': (r) => r.status === 200,
    '[Feedbacks] response time <= 500ms': (r) => r.timings.duration <= 500,
  });
  // errorRateFeedbacks.add(!feedbacksSuccess);

  check(responses[2], {
    '[Statistic] status is 200': (r) => r.status === 200,
    '[Statistic] response time <= 500ms': (r) => r.timings.duration <= 500,
  });
  // errorRateStatistic.add(!statisticSuccess);

  // 전체 batch 응답시간 측정 (sleep 제외)
  const batchElapsedTime = (new Date().getTime() - startTime); // ms 단위
  batchResponseTime.add(batchElapsedTime);

  // 전체 요청 체크 (3개 API batch를 1개 요청으로 간주)
  check(null, {
    'total requests': () => true,
    'batch status all 200': () =>
      responses[0].status === 200 &&
      responses[1].status === 200 &&
      responses[2].status === 200,
    'batch has non-200': () =>
      responses[0].status !== 200 ||
      responses[1].status !== 200 ||
      responses[2].status !== 200,
    'batch response time > 500ms': () => batchElapsedTime > 500,
    'batch response time <= 500ms': () => batchElapsedTime <= 500,
  });

  // 총 사이클 시간을 0.7초로 맞추기
  const elapsedTime = batchElapsedTime / 1000; // 초 단위
  const remainingTime = 0.7 - elapsedTime;
  if (remainingTime > 0) {
    sleep(remainingTime);
  }
}
