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
    console.log('[FCM Hook] 초기 저장된 알림 상태:', storedState);
    return storedState;
  });

  useEffect(() => {
    const storedState = getStoredNotificationState();
    console.log('[FCM Hook] useEffect - 저장된 알림 상태 확인:', storedState);
    setIsEnabled(storedState);
  }, []);

  useEffect(() => {
    if (typeof window === 'undefined' || !('Notification' in window)) {
      console.log('[FCM Hook] 브라우저에서 Notification API를 지원하지 않음');
      return;
    }

    const currentPermission = Notification.permission;
    console.log('[FCM Hook] 현재 알림 권한:', currentPermission);
    setPermission(currentPermission);

    const interval = setInterval(() => {
      const updatedPermission = Notification.permission;
      if (updatedPermission !== permission) {
        console.log('[FCM Hook] 알림 권한 변경됨:', updatedPermission);
        setPermission(updatedPermission);
      }
    }, 1000);

    return () => {
      console.log('[FCM Hook] 권한 확인 interval 정리');
      clearInterval(interval);
    };
  }, [permission]);

  useEffect(() => {
    if (!messaging) {
      console.log(
        '[FCM Hook] messaging 객체가 없어서 포그라운드 메시지 리스너 설정 불가'
      );
      return () => {};
    }

    console.log('[FCM Hook] 포그라운드 메시지 리스너 설정 중...');

    try {
      const unsubscribe = onMessage(messaging, (payload: MessagePayload) => {
        console.log('[FCM Hook] 포그라운드 메시지 수신:', payload);

        if (
          typeof window !== 'undefined' &&
          'Notification' in window &&
          Notification.permission === 'granted'
        ) {
          console.log('[FCM Hook] 브라우저 알림 표시 중...');
          new Notification(payload.notification?.title || '새 알림', {
            body: payload.notification?.body || '새로운 메시지가 있습니다.',
            icon: payload.notification?.icon || '/logo192.png',
            data: payload.data,
          });
        } else {
          console.log(
            '[FCM Hook] 브라우저 알림 표시 불가 - 권한:',
            Notification.permission
          );
        }
      });

      console.log('[FCM Hook] 포그라운드 메시지 리스너 설정 완료');
      return () => {
        console.log('[FCM Hook] 포그라운드 메시지 리스너 해제');
        unsubscribe();
      };
    } catch (error) {
      console.error('[FCM Hook] 포그라운드 메시지 수신 설정 중 오류:', error);
      return () => {};
    }
  }, []);

  const updateState = (enabled: boolean) => {
    console.log('[FCM Hook] 상태 업데이트:', {
      이전: isEnabled,
      변경후: enabled,
    });
    setIsEnabled(enabled);
    setStoredNotificationState(enabled);
  };

  const storedToken = getStoredFCMToken();
  console.log('[FCM Hook] 현재 저장된 FCM 토큰 존재 여부:', !!storedToken);
  console.log('[FCM Hook] 저장된 토큰 타입:', typeof storedToken);
  if (storedToken && typeof storedToken === 'string') {
    console.log(
      '[FCM Hook] 저장된 FCM 토큰 (일부):',
      storedToken.substring(0, 20) + '...'
    );
  } else if (storedToken) {
    console.log('[FCM Hook] 저장된 토큰 (전체):', storedToken);
  }

  const fcmStatus: FCMStatus = {
    isSupported,
    permission,
    hasToken: storedToken !== null,
  };

  console.log('[FCM Hook] 현재 FCM 상태:', {
    지원여부: isSupported,
    권한: permission,
    토큰존재: fcmStatus.hasToken,
    활성화: isEnabled,
  });

  return {
    fcmStatus,
    permission,
    isSupported,
    isEnabled,
    updateState,
  };
};
