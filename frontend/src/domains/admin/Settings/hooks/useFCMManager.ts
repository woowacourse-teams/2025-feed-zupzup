import { useState, useEffect } from 'react';
import { onMessage, MessagePayload } from 'firebase/messaging';
import { messaging } from '@/firebase/messaging';
import { NotificationService } from '@/services/notificationService';
import {
  getStoredNotificationState,
  setStoredNotificationState,
  getStoredFCMToken,
} from '@/utils/notificationUtils';
import type { FCMStatus } from '@/types/notification.types';

export const useFCMManager = () => {
  const [permission, setPermission] =
    useState<NotificationPermission>('default');
  const isSupported = NotificationService.checkIsSupported();

  const isIOSBrowser = () => {
    const userAgent = window.navigator.userAgent.toLowerCase();
    const isIOS = /iphone|ipad|ipod/.test(userAgent);
    const isStandalone = window.matchMedia(
      '(display-mode: standalone)'
    ).matches;
    return isIOS && !isStandalone;
  };

  const needsPWAInstall = isIOSBrowser();

  const [isEnabled, setIsEnabled] = useState(() => {
    const storedState = getStoredNotificationState();
    return storedState;
  });

  useEffect(() => {
    const storedState = getStoredNotificationState();
    setIsEnabled(storedState);
  }, []);

  useEffect(() => {
    if (typeof window === 'undefined' || !('Notification' in window)) {
      return;
    }

    const currentPermission = Notification.permission;
    setPermission(currentPermission);

    const interval = setInterval(() => {
      const updatedPermission = Notification.permission;
      if (updatedPermission !== permission) {
        setPermission(updatedPermission);
      }
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, [permission]);

  useEffect(() => {
    if (!messaging) {
      return () => {};
    }

    try {
      const unsubscribe = onMessage(messaging, (payload: MessagePayload) => {
        const isPWA = window.matchMedia('(display-mode: standalone)').matches;

        if (!isPWA) {
          if (
            typeof window !== 'undefined' &&
            'Notification' in window &&
            Notification.permission === 'granted'
          ) {
            const notification = new Notification(
              payload.data?.title || '새 알림',
              {
                body: payload.data?.body || '새로운 메시지가 있습니다.',
                icon: payload.data?.icon || '/192x192.png',
                data: payload.data,
              }
            );

            notification.onclick = () => {
              notification.close();
              window.focus();
              window.location.href = '/';
            };
          }
        }
      });

      return () => {
        unsubscribe();
      };
    } catch (error) {
      console.error('[FCM Hook] 포그라운드 메시지 리스너 설정 오류:', error);
      return () => {};
    }
  }, []);

  const updateState = (enabled: boolean) => {
    setIsEnabled(enabled);
    setStoredNotificationState(enabled);
  };

  const storedToken = getStoredFCMToken();

  const fcmStatus: FCMStatus = {
    isSupported,
    permission,
    hasToken: storedToken !== null,
  };

  return {
    fcmStatus,
    permission,
    isSupported,
    isEnabled,
    updateState,
    needsPWAInstall,
  };
};
