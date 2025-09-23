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
        preset: 'desktop',
        formFactor: 'desktop',
        throttlingMethod: 'simulate',
        screenEmulation: {
          mobile: false,
          width: 1365,
          height: 935,
          deviceScaleFactor: 1,
          disabled: false,
        },
        disableStorageReset: true,
        blockedUrlPatterns: [
          '*googletagmanager.com*',
          '*google-analytics.com*',
          '*doubleclick.net*',
          '*ads*',
          '*hotjar*',
        ],
        chromeFlags: [
          '--headless=new',
          '--no-sandbox',
          '--disable-dev-shm-usage',
          '--disable-background-networking',
          '--disable-default-apps',
          '--disable-extensions',
          '--no-first-run',
          '--no-default-browser-check',
          '--window-size=1365,935',
          '--disable-mobile-emulation',
        ],
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
