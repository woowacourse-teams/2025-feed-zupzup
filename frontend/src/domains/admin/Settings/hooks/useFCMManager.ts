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

      return () => {
        unsubscribe();
      };
    } catch (error) {
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
  };
};
