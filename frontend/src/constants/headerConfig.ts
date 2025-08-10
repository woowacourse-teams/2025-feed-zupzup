export interface HeaderConfig {
  title: string;
  subtitle: string;
  showMoreIcon: boolean;
  showBackButton: boolean;
}

export const HEADER_EXCEPT_PATHS = [
  '/login',
  '/signup',
  '/dashboard',
  '/onboarding',
];

export const HEADER_CONFIGS: Record<string, HeaderConfig> = {
  '/admin': {
    title: '피드백 관리',
    subtitle: '피드백 현황 및 관리',
    showMoreIcon: true,
    showBackButton: true,
},
  '/settings': {
    title: '설정',
    subtitle: '계정 및 앱 설정',
    showMoreIcon: true,
    showBackButton: true,
  },
  '/notifications': {
    title: '알림 설정',
    subtitle: '알림 확인 및 관리',
    showMoreIcon: false,
    showBackButton: true,
  },
};
