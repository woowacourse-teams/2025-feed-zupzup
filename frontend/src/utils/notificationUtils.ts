import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';

const NOTIFICATION_KEY = 'notification_enabled';
const FCM_TOKEN_KEY = 'fcm_token';

export const getStoredNotificationState = (): boolean => {
  const stored = getLocalStorage<boolean>(NOTIFICATION_KEY);
  console.log('[NotificationUtils] getStoredNotificationState:', {
    key: NOTIFICATION_KEY,
    stored,
    type: typeof stored,
  });
  return stored ?? false;
};

export const setStoredNotificationState = (enabled: boolean): void => {
  console.log('[NotificationUtils] setStoredNotificationState:', {
    key: NOTIFICATION_KEY,
    enabled,
    type: typeof enabled,
  });
  setLocalStorage(NOTIFICATION_KEY, enabled);

  // 저장 후 검증
  const verification = getLocalStorage<boolean>(NOTIFICATION_KEY);
  console.log('[NotificationUtils] 저장 후 검증:', {
    원본: enabled,
    저장된값: verification,
    일치: enabled === verification,
  });
};

export const getStoredFCMToken = (): string | null => {
  const rawValue = getLocalStorage<string>(FCM_TOKEN_KEY);
  console.log('[NotificationUtils] getStoredFCMToken 원시값:', {
    key: FCM_TOKEN_KEY,
    rawValue,
    type: typeof rawValue,
    isArray: Array.isArray(rawValue),
    length: rawValue?.length,
  });

  // localStorage에서 직접 확인
  if (typeof window !== 'undefined') {
    const directValue = localStorage.getItem(FCM_TOKEN_KEY);
    console.log('[NotificationUtils] localStorage 직접 조회:', {
      directValue,
      type: typeof directValue,
    });

    // 직접 조회한 값이 문자열이고 getLocalStorage 결과와 다르면 직접 반환
    if (
      directValue &&
      typeof directValue === 'string' &&
      directValue !== 'null'
    ) {
      console.log('[NotificationUtils] localStorage에서 직접 가져온 값 사용');
      return directValue;
    }
  }

  // 문자열이 아니거나 빈 값이면 null 반환
  if (!rawValue || typeof rawValue !== 'string' || rawValue.length === 0) {
    console.log('[NotificationUtils] 유효하지 않은 토큰, null 반환');
    return null;
  }

  console.log('[NotificationUtils] 유효한 토큰 반환:', {
    tokenPrefix: rawValue.substring(0, 20) + '...',
    fullLength: rawValue.length,
  });

  return rawValue;
};

export const setStoredFCMToken = (token: string): void => {
  console.log('[NotificationUtils] setStoredFCMToken 시작:', {
    key: FCM_TOKEN_KEY,
    token: token ? token.substring(0, 20) + '...' : token,
    type: typeof token,
    length: token?.length,
  });

  if (!token || typeof token !== 'string') {
    console.error('[NotificationUtils] 잘못된 토큰 타입:', {
      token,
      type: typeof token,
    });
    return;
  }

  setLocalStorage(FCM_TOKEN_KEY, token);

  // 저장 후 검증
  const verification = getLocalStorage<string>(FCM_TOKEN_KEY);
  console.log('[NotificationUtils] 토큰 저장 후 검증:', {
    원본길이: token.length,
    저장된길이: verification?.length,
    저장된타입: typeof verification,
    일치: token === verification,
    저장된값미리보기: verification
      ? verification.substring(0, 20) + '...'
      : verification,
  });

  // localStorage에서 직접 확인
  if (typeof window !== 'undefined') {
    const directValue = localStorage.getItem(FCM_TOKEN_KEY);
    console.log('[NotificationUtils] localStorage 직접 저장 확인:', {
      directValue: directValue
        ? directValue.substring(0, 20) + '...'
        : directValue,
      directType: typeof directValue,
      directLength: directValue?.length,
    });
  }
};

export const clearStoredFCMToken = (): void => {
  console.log('[NotificationUtils] clearStoredFCMToken 시작');

  // 삭제 전 현재 값 확인
  const beforeClear = getLocalStorage<string>(FCM_TOKEN_KEY);
  console.log('[NotificationUtils] 삭제 전 토큰:', {
    exists: !!beforeClear,
    type: typeof beforeClear,
  });

  setLocalStorage(FCM_TOKEN_KEY, null);

  // 삭제 후 검증
  const afterClear = getLocalStorage<string>(FCM_TOKEN_KEY);
  console.log('[NotificationUtils] 삭제 후 검증:', {
    afterClear,
    type: typeof afterClear,
    isNull: afterClear === null,
  });

  // localStorage에서 직접 확인
  if (typeof window !== 'undefined') {
    const directValue = localStorage.getItem(FCM_TOKEN_KEY);
    console.log('[NotificationUtils] localStorage 직접 삭제 확인:', {
      directValue,
      type: typeof directValue,
    });
  }
};

export const isNotificationSupported = (): boolean => {
  const supported =
    typeof window !== 'undefined' &&
    'serviceWorker' in navigator &&
    'Notification' in window;

  console.log('[NotificationUtils] isNotificationSupported:', {
    window: typeof window !== 'undefined',
    serviceWorker:
      typeof window !== 'undefined' && 'serviceWorker' in navigator,
    notification: typeof window !== 'undefined' && 'Notification' in window,
    전체지원: supported,
  });

  return supported;
};

export const createNotificationErrorMessage = (error: unknown): string => {
  console.log('[NotificationUtils] createNotificationErrorMessage:', {
    error,
    type: typeof error,
    isError: error instanceof Error,
  });

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
