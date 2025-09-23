module.exports = {
  LHCI_GOOGLE_SPREAD_SHEET_ID: process.env.LHCI_GOOGLE_SPREAD_SHEET_ID,

  LHCI_GREEN_MIN_SCORE: 90,
  LHCI_ORANGE_MIN_SCORE: 50,
  LHCI_RED_MIN_SCORE: 0,

  LHCI_MONITORING_PAGE_NAMES: [
    'feedback_submit',
    'feedback_dashboard',
    'admin_home',
    'admin_dashboard',
    'admin_settings',
  ],

  LHCI_PAGE_NAME_TO_URL: {
    feedback_submit: '/d0b1b979-7ae8-11f0-8408-0242ac120002/submit',
    feedback_dashboard: '/d0b1b979-7ae8-11f0-8408-0242ac120002/dashboard',
    admin_home: '/admin/home',
    admin_dashboard: '/admin/d0b1b979-7ae8-11f0-8408-0242ac120002/dashboard',
    admin_settings: '/admin/settings',
  },
  LHCI_PAGE_NAME_TO_SHEET_ID: {
    feedback_submit: 0,
    feedback_dashboard: 939688316,
    admin_home: 1333922864,
    admin_dashboard: 1766877014,
    admin_settings: 1981432470,
  },

  getLhciPageNameFromUrl: (url) => {
    for (const [name, path] of Object.entries(
      module.exports.LHCI_PAGE_NAME_TO_URL
    )) {
      if (decodeURIComponent(path) === decodeURIComponent(url)) {
        return name;
      }
    }
    return undefined;
  },

  getLhciUrlFromPageName: (name) => {
    return module.exports.LHCI_PAGE_NAME_TO_URL[name];
  },

  getLhciSheetIdFromPageName: (name) => {
    return module.exports.LHCI_PAGE_NAME_TO_SHEET_ID[name];
  },
};
