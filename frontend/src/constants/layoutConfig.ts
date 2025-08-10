export interface HeaderConfig {
  show: boolean;
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
      show: true,
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
      show: true,
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
      show: true,
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
