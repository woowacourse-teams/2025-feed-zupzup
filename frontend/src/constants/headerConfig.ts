export interface HeaderConfig {
  title: string;
  subtitle: string;
  showMoreIcon: boolean;
}

export const HEADER_EXCEPT_PATHS = [
  '/login',
  '/signup',
  '/dashboard',
  '/onboarding',
];

export const HEADER_CONFIGS: Record<string, HeaderConfig> = {
  '/': {
    title: 'Home',
    subtitle: 'Welcome',
    showMoreIcon: false,
  },
  '/admin': {
    title: '피드백 관리',
    subtitle: '피드백 현황 및 관리',
    showMoreIcon: true,
  },
  '/settings': {
    title: '설정',
    subtitle: '계정 및 앱 설정',
    showMoreIcon: true,
  },
  '/notifications': {
    title: '알림 설정',
    subtitle: '알림 확인 및 관리',
    showMoreIcon: false,
  },
};

export const DEFAULT_HEADER_CONFIG: HeaderConfig = {
  title: 'Feedback',
  subtitle: 'Feedback',
  showMoreIcon: false,
};
