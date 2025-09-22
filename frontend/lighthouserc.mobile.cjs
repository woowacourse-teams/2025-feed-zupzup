const {
  LHCI_MONITORING_PAGE_NAMES,
  getLhciUrlFromPageName,
} = require('./lighthouse.config.cjs');

// URL 목록 생성
const urls = LHCI_MONITORING_PAGE_NAMES.map(
  (name) => `http://localhost:3000${getLhciUrlFromPageName(name)}`
);

module.exports = {
  ci: {
    collect: {
      // 개발 서버 시작 명령어
      startServerCommand: 'npm run dev',

      // 검사할 URL 목록
      url: urls,

      numberOfRuns: 1, // lighthouse 검사 실행 횟수

      // lighthouse 검사 설정 (모바일 전용)
      settings: {
        formFactor: 'mobile',
        chromeFlags: [
          '--headless',
          '--disable-gpu',
          '--no-sandbox',
          '--disable-dev-shm-usage',
        ],
        screenEmulation: {
          mobile: true,
          width: 375,
          height: 667,
          deviceScaleFactor: 2,
          disabled: false,
        },
        throttling: {
          rttMs: 150,
          throughputKbps: 1638.4,
          cpuSlowdownMultiplier: 4,
          requestLatencyMs: 150,
          downloadThroughputKbps: 1638.4,
          uploadThroughputKbps: 675,
        },
      },
    },

    assert: {
      // 각 카테고리별 성능 점수 기준 설정 (모바일은 기준을 낮춤)
      assertions: {
        'categories:performance': ['warn', { minScore: 0.6 }], // 모바일은 60점
        'categories:accessibility': ['warn', { minScore: 0.9 }],
        'categories:best-practices': ['warn', { minScore: 0.9 }],
        'categories:seo': ['warn', { minScore: 0.9 }],
      },
    },

    upload: {
      target: 'filesystem',
      outputDir: './lighthouse-results-mobile', // 모바일 결과는 별도 폴더
      reportFilenamePattern:
        '%%PATHNAME%%-%%DATETIME%%-mobile-report.%%EXTENSION%%',
    },
  },
};
