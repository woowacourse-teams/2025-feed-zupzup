const {
  LHCI_MONITORING_PAGE_NAMES,
  getLhciUrlFromPageName,
} = require('./lighthouse.config.cjs');

const paths = LHCI_MONITORING_PAGE_NAMES.map(getLhciUrlFromPageName);

module.exports = {
  ci: {
    collect: {
      staticDistDir: './dist',
      isSinglePageApplication: true,
      url: paths.length ? paths : ['/'],
      numberOfRuns: 1,
      settings: {
        preset: 'mobile', // 이거면 충분함
        chromeFlags: [
          '--headless',
          '--no-sandbox',
          '--disable-gpu',
          '--disable-dev-shm-usage',
        ],
      },
    },
    assert: {
      assertions: {
        'categories:performance': ['warn', { minScore: 0.6 }],
        'categories:accessibility': ['warn', { minScore: 0.9 }],
        'categories:best-practices': ['warn', { minScore: 0.9 }],
        'categories:seo': ['warn', { minScore: 0.9 }],
      },
    },
    upload: {
      target: 'filesystem',
      outputDir: './lighthouse-results-mobile',
      reportFilenamePattern:
        '%%PATHNAME%%-%%DATETIME%%-mobile-report.%%EXTENSION%%',
    },
  },
};
