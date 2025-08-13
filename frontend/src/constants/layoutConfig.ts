import { ROUTES } from './routes';

interface HeaderConfig {
  show: boolean;
  title?: string;
  subtitle?: string;
  hasMoreIcon?: boolean;
  showBackButton?: boolean;
}

interface BottomNavConfig {
  show: boolean;
}

export interface LayoutConfig {
  header: HeaderConfig;
  bottomNav: BottomNavConfig;
}

export const LAYOUT_CONFIGS: Record<string, LayoutConfig> = {
  [ROUTES.ADMIN]: {
    header: {
      show: true,
      title: '피드백 관리',
      subtitle: '피드백 현황 및 관리',
      hasMoreIcon: true,
      showBackButton: true,
    },
    bottomNav: {
      show: true,
    },
  },
  [ROUTES.ADMIN_SETTINGS]: {
    header: {
      show: true,
      title: '설정',
      subtitle: '계정 및 앱 설정',
      hasMoreIcon: false,
      showBackButton: false,
    },
    bottomNav: {
      show: true,
    },
  },
  [ROUTES.NOTIFICATIONS]: {
    header: {
      show: true,
      title: '알림 설정',
      subtitle: '알림 확인 및 관리',
      hasMoreIcon: false,
      showBackButton: true,
    },
    bottomNav: {
      show: false,
    },
  },
  [ROUTES.ADMIN_HOME]: {
    header: {
      show: false,
    },
    bottomNav: {
      show: true,
    },
  },
};
