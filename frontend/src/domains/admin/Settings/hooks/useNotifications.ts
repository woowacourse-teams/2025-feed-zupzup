import { useState, useEffect } from 'react';
import { onMessage, MessagePayload } from 'firebase/messaging';
import { messaging } from '@/firebase/messaging';
import { NotificationService } from '@/services';
import {
  getStoredNotificationState,
  setStoredNotificationState,
} from '@/utils/notificationUtils';
import type { FCMStatus } from '@/types/notification.types';

export const useNotifications = (organizationId: number) => {
  const [permission, setPermission] =
    useState<NotificationPermission>('default');
  const [isSupported] = useState(() => NotificationService.isSupported());

  const [isEnabled, setIsEnabled] = useState(() =>
    getStoredNotificationState(organizationId)
  );

  useEffect(() => {
    setIsEnabled(getStoredNotificationState(organizationId));
  }, [organizationId]);

  useEffect(() => {
    if (typeof window === 'undefined' || !('Notification' in window)) return;

    setPermission(Notification.permission);

    const interval = setInterval(() => {
      setPermission(Notification.permission);
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (!messaging) return () => {};

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
      return () => {};
    }
  }, []);

  const updateState = (enabled: boolean) => {
    setIsEnabled(enabled);
    setStoredNotificationState(organizationId, enabled);
  };

  const fcmStatus: FCMStatus = {
    isSupported,
    permission,
    hasToken: false,
  };

  return {
    fcmStatus,
    permission,
    isSupported,

    isEnabled,
    updateState,
  };
};
