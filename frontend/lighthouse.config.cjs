
module.exports = {
  // Google Spreadsheet ID (나중에 필요시)
  LHCI_GOOGLE_SPREAD_SHEET_ID: process.env.LHCI_GOOGLE_SPREAD_SHEET_ID,

  // Lighthouse 점수 색상 기준
  LHCI_GREEN_MIN_SCORE: 90,
  LHCI_ORANGE_MIN_SCORE: 50,
  LHCI_RED_MIN_SCORE: 0,

  // 모니터링할 페이지 이름 목록
  LHCI_MONITORING_PAGE_NAMES: [
    'feedback_submit', // 피드백 작성
    'feedback_dashboard', // 피드백 대시보드
    'admin_home', // 관리자 방 목록
    'admin_dashboard', // 관리자 방 대시보드
    'admin_settings', // 관리자 설정 페이지
  ],

  // 페이지 이름 - URL 매핑
  LHCI_PAGE_NAME_TO_URL: {
    feedback_submit: '/d0b1b979-7ae8-11f0-8408-0242ac120002/submit', // :id → 실제 UUID
    feedback_dashboard: '/d0b1b979-7ae8-11f0-8408-0242ac120002/dashboard', // :id → 실제 UUID
    admin_home: '/admin/home',
    admin_dashboard: '/admin/d0b1b979-7ae8-11f0-8408-0242ac120002/dashboard', // :id → 실제 UUID
    admin_settings: '/admin/settings',
  },
  // 페이지 이름 - 시트 ID 매핑 (구글 시트용)
  LHCI_PAGE_NAME_TO_SHEET_ID: {
    feedback_submit: 0,
    feedback_dashboard: 1,
    admin_home: 2,
    admin_dashboard: 3,
    admin_settings: 4,
  },

  // URL에서 페이지 이름 찾기
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

  // 페이지 이름에서 URL 가져오기
  getLhciUrlFromPageName: (name) => {
    return module.exports.LHCI_PAGE_NAME_TO_URL[name];
  },

  // 페이지 이름에서 시트 ID 가져오기
  getLhciSheetIdFromPageName: (name) => {
    return module.exports.LHCI_PAGE_NAME_TO_SHEET_ID[name];
  },
};
