import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';

const NOTIFICATION_KEY = 'notification_enabled';
const FCM_TOKEN_KEY = 'fcm_token';

export const getStoredNotificationState = (): boolean => {
  const stored = getLocalStorage<boolean>(NOTIFICATION_KEY);
  return stored ?? false;
};

export const setStoredNotificationState = (enabled: boolean): void => {
  setLocalStorage(NOTIFICATION_KEY, enabled);

  getLocalStorage<boolean>(NOTIFICATION_KEY);
};

export const getStoredFCMToken = (): string | null => {
  const rawValue = getLocalStorage<string>(FCM_TOKEN_KEY);

  if (typeof window !== 'undefined') {
    const directValue = localStorage.getItem(FCM_TOKEN_KEY);

    if (
      directValue &&
      typeof directValue === 'string' &&
      directValue !== 'null'
    ) {
      return directValue;
    }
  }

  if (!rawValue || typeof rawValue !== 'string' || rawValue.length === 0) {
    return null;
  }

  return rawValue;
};

export const setStoredFCMToken = (token: string): void => {
  if (!token || typeof token !== 'string') {
    console.error('[NotificationUtils] 잘못된 토큰 타입:', {
      token,
      type: typeof token,
    });
    return;
  }

  setLocalStorage(FCM_TOKEN_KEY, token);

  getLocalStorage<string>(FCM_TOKEN_KEY);

  if (typeof window !== 'undefined') {
    localStorage.getItem(FCM_TOKEN_KEY);
  }
};

export const clearStoredFCMToken = (): void => {
  getLocalStorage<string>(FCM_TOKEN_KEY);

  setLocalStorage(FCM_TOKEN_KEY, null);

  getLocalStorage<string>(FCM_TOKEN_KEY);

  if (typeof window !== 'undefined') {
    localStorage.getItem(FCM_TOKEN_KEY);
  }
};

export const isNotificationSupported = (): boolean => {
  const supported =
    typeof window !== 'undefined' &&
    'serviceWorker' in navigator &&
    'Notification' in window;

  return supported;
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
