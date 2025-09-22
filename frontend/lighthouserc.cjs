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

      // lighthouse 검사 설정 (데스크톱 전용)
      settings: {
        preset: 'desktop',
        formFactor: 'desktop',
        chromeFlags: [
          '--disable-mobile-emulation',
          '--headless',
          '--disable-gpu',
          '--no-sandbox',
          '--disable-dev-shm-usage',
        ],
        screenEmulation: {
          mobile: false,
          width: 1350,
          height: 940,
          deviceScaleFactor: 1,
          disabled: false,
        },
      },
    },

    assert: {
      // 각 카테고리별 성능 점수 기준 설정
      assertions: {
        'categories:performance': ['warn', { minScore: 0.8 }],
        'categories:accessibility': ['warn', { minScore: 0.9 }],
        'categories:best-practices': ['warn', { minScore: 0.9 }],
        'categories:seo': ['warn', { minScore: 0.9 }],
      },
    },

    upload: {
      target: 'filesystem',
      outputDir: './lighthouse-results',
      reportFilenamePattern: '%%PATHNAME%%-%%DATETIME%%-report.%%EXTENSION%%',
    },
  },
};
