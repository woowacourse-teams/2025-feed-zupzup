import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';

const NOTIFICATION_KEY = 'notification_enabled';
const FCM_TOKEN_KEY = 'fcm_token';

export const getStoredNotificationState = (): boolean => {
  const stored = getLocalStorage<boolean>(NOTIFICATION_KEY);
  return stored ?? false;
};

export const setStoredNotificationState = (enabled: boolean): void => {
  setLocalStorage(NOTIFICATION_KEY, enabled);
};

export const getStoredFCMToken = (): string | null => {
  return getLocalStorage<string>(FCM_TOKEN_KEY) ?? null;
};

export const setStoredFCMToken = (token: string): void => {
  setLocalStorage(FCM_TOKEN_KEY, token);
};

export const clearStoredFCMToken = (): void => {
  setLocalStorage(FCM_TOKEN_KEY, null);
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
