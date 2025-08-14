import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';

const getNotificationKey = (organizationId: number) =>
  `notification_enabled_${organizationId}`;

export const getStoredNotificationState = (organizationId: number): boolean => {
  const key = getNotificationKey(organizationId);
  const stored = getLocalStorage<boolean>(key);
  return stored ?? false;
};

export const setStoredNotificationState = (
  organizationId: number,
  enabled: boolean
): void => {
  const key = getNotificationKey(organizationId);
  setLocalStorage(key, enabled);
};

export const isNotificationSupported = (): boolean => {
  return (
    typeof window !== 'undefined' &&
    'serviceWorker' in navigator &&
    'Notification' in window
  );
};

export const createNotificationErrorMessage = (error: unknown): string => {
  if (error instanceof Error) {
    if (error.message.includes('권한')) {
      return '알림 권한을 허용해주세요.';
    }
    if (error.message.includes('토큰')) {
      return '알림 설정 중 오류가 발생했습니다. 다시 시도해주세요.';
    }
    return error.message;
  }
  return '알림 설정 변경에 실패했습니다.';
};
