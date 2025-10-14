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
  [`${ROUTES.ADMIN}/${ROUTES.DASHBOARD}`]: {
    header: {
      show: true,
      title: '피드백 관리',
      subtitle: '피드백 현황 및 관리',
      hasMoreIcon: true,
      showBackButton: true,
    },
    bottomNav: {
      show: false,
    },
  },
  [`${ROUTES.ADMIN}/${ROUTES.ADMIN_SETTINGS}`]: {
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
  [`${ROUTES.ADMIN}/${ROUTES.ADMIN_HOME}`]: {
    header: {
      show: false,
    },
    bottomNav: {
      show: true,
    },
  },
  [`${ROUTES.ADMIN}/${ROUTES.AI_SUMMARY}`]: {
    header: {
      show: true,
      title: 'AI 피드백',
      subtitle: 'AI가 찾은 비슷한 피드백',
      hasMoreIcon: true,
      showBackButton: true,
    },
    bottomNav: {
      show: false,
    },
  },
};
