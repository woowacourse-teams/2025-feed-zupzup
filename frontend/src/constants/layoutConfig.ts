export interface HeaderConfig {
  title: string;
  subtitle: string;
  showMoreIcon: boolean;
  showBackButton: boolean;
}

export interface BottomNavConfig {
  show: boolean;
}

export interface LayoutConfig {
  header: HeaderConfig;
  bottomNav: BottomNavConfig;
}

export const LAYOUT_CONFIGS: Record<string, LayoutConfig> = {
  '/admin': {
    header: {
      title: '피드백 관리',
      subtitle: '피드백 현황 및 관리',
      showMoreIcon: true,
      showBackButton: false,
    },
    bottomNav: {
      show: true,
    },
  },
  '/settings': {
    header: {
      title: '설정',
      subtitle: '계정 및 앱 설정',
      showMoreIcon: false,
      showBackButton: false,
    },
    bottomNav: {
      show: true,
    },
  },
  '/notifications': {
    header: {
      title: '알림 설정',
      subtitle: '알림 확인 및 관리',
      showMoreIcon: false,
      showBackButton: true,
    },
    bottomNav: {
      show: false,
    },
  },
};

export const DEFAULT_LAYOUT_CONFIG: LayoutConfig = {
  header: {
    title: 'Feedback',
    subtitle: 'Feedback',
    showMoreIcon: false,
    showBackButton: false,
  },
  bottomNav: {
    show: false,
  },
};
