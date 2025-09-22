const {
  LHCI_MONITORING_PAGE_NAMES,
  getLhciUrlFromPageName,
} = require('./lighthouse.config.cjs');

const urls = LHCI_MONITORING_PAGE_NAMES.map(
  (name) => `http://localhost:3000${getLhciUrlFromPageName(name)}`
);

module.exports = {
  ci: {
    collect: {
      startServerCommand: 'npm run dev',
      url: urls,
      numberOfRuns: 1,
      settings: {
        formFactor: 'mobile',
        chromeFlags: [
          '--headless',
          '--disable-gpu',
          '--no-sandbox',
          '--disable-dev-shm-usage',
        ],
        chromePath: '/usr/bin/google-chrome',
        screenEmulation: {
          mobile: true,
          width: 375,
          height: 667,
          deviceScaleFactor: 2,
          disabled: false,
        },
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
