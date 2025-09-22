const {
  LHCI_MONITORING_PAGE_NAMES,
  getLhciUrlFromPageName,
} = require('./lighthouse.config.cjs');

const paths = LHCI_MONITORING_PAGE_NAMES.map(getLhciUrlFromPageName);

module.exports = {
  ci: {
    collect: {
      // ✅ 빌드 산출물 폴더를 LHCI가 정적 서빙
      staticDistDir: './dist',
      url: paths.length ? paths : ['/', '/guide', '/introduce'],
      numberOfRuns: 1,
      settings: {
        preset: 'desktop',
        formFactor: 'desktop',
        screenEmulation: {
          mobile: false,
          width: 1350,
          height: 940,
          deviceScaleFactor: 1,
          disabled: false,
        },
        chromeFlags: ['--disable-mobile-emulation'],
      },
    },
    assert: {
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
