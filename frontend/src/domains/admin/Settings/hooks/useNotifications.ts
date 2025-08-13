import { useState, useEffect, useCallback } from 'react';
import { getToken, onMessage, MessagePayload } from 'firebase/messaging';
import { messaging } from '@/firebase/messaging';
import { VAPID_KEY } from '@/firebase/config';

interface UseNotificationsReturn {
  token: string | null;
  permission: NotificationPermission;
  isSupported: boolean;
  loading: boolean;
  generateToken: () => Promise<string>;
  requestPermission: () => Promise<NotificationPermission>;
}

export const useNotifications = (): UseNotificationsReturn => {
  const [token, setToken] = useState<string | null>(null);
  const [permission, setPermission] =
    useState<NotificationPermission>('default');
  const [isSupported, setIsSupported] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    try {
      if (typeof window === 'undefined') {
        setIsSupported(false);
        return;
      }

      const supported =
        'serviceWorker' in navigator && 'Notification' in window && !!messaging;

      setIsSupported(supported);
    } catch (error) {
      console.error('[FCM Hook] 지원 여부 확인 중 오류:', error);
      setIsSupported(false);
    }
  }, []);

  const registerServiceWorker =
    useCallback(async (): Promise<ServiceWorkerRegistration> => {
      if (!('serviceWorker' in navigator)) {
        throw new Error('서비스 워커를 지원하지 않는 브라우저입니다.');
      }
      const existing = await navigator.serviceWorker.getRegistration();
      if (existing) {
        return existing;
      }

      const registration =
        await navigator.serviceWorker.register('/service-worker.js');
      return registration;
    }, []);

  const requestPermission =
    useCallback(async (): Promise<NotificationPermission> => {
      if (typeof window === 'undefined' || !('Notification' in window)) {
        throw new Error('이 브라우저는 알림을 지원하지 않습니다.');
      }

      if (!isSupported) {
        throw new Error('이 브라우저는 푸시 알림을 지원하지 않습니다.');
      }

      const result = await Notification.requestPermission();
      setPermission(result);

      if (result !== 'granted') {
        throw new Error('알림 권한이 거부되었습니다.');
      }

      return result;
    }, [isSupported]);

  const generateToken = useCallback(async (): Promise<string> => {
    if (!messaging || !VAPID_KEY) {
      throw new Error('Firebase 설정이 올바르지 않습니다.');
    }
    setLoading(true);

    try {
      const registration =
        (await navigator.serviceWorker.getRegistration()) ??
        (await registerServiceWorker());

      if (permission !== 'granted') {
        await requestPermission();
      }

      const currentToken = await getToken(messaging, {
        vapidKey: VAPID_KEY,
        serviceWorkerRegistration: registration,
      });

      if (!currentToken) {
        throw new Error('FCM 토큰 생성에 실패했습니다.');
      }

      setToken(currentToken);
      return currentToken;
    } finally {
      setLoading(false);
    }
  }, [permission, registerServiceWorker, requestPermission]);

  useEffect(() => {
    if (!messaging) return;

    try {
      const unsubscribe = onMessage(messaging, (payload: MessagePayload) => {
        if (
          typeof window !== 'undefined' &&
          'Notification' in window &&
          Notification.permission === 'granted'
        ) {
          new Notification(payload.notification?.title || '새 알림', {
            body: payload.notification?.body || '새로운 메시지가 있습니다.',
            icon: payload.notification?.icon || '/logo192.png',
            data: payload.data,
          });
        }
      });

      return () => unsubscribe();
    } catch (error) {
      console.error('[FCM Hook] 포그라운드 메시지 수신 설정 중 오류:', error);
    }
  }, []);

  useEffect(() => {
    if (typeof window !== 'undefined' && 'Notification' in window) {
      setPermission(Notification.permission);

      const interval = setInterval(() => {
        setPermission(Notification.permission);
      }, 1000);

      return () => clearInterval(interval);
    }
  }, []);

  return {
    token,
    permission,
    isSupported,
    loading,
    generateToken,
    requestPermission,
  };
};
