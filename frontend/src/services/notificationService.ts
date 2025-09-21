import { getToken } from 'firebase/messaging';
import { messaging } from '@/firebase/messaging';
import { VAPID_KEY } from '@/firebase/config';
import {
  postFCMToken,
  getNotificationSettings,
} from '@/apis/notifications.api';
import {
  setStoredNotificationState,
  isNotificationSupported,
  createNotificationErrorMessage,
  getStoredFCMToken,
  setStoredFCMToken,
  clearStoredFCMToken,
} from '@/utils/notificationUtils';
import type { NotificationServiceResult } from '@/types/notification.types';

export class NotificationService {
  static async enable(): Promise<NotificationServiceResult> {
    try {
      // 지원 여부 확인
      const supportCheck = isNotificationSupported();

      if (!supportCheck) {
        throw new Error('이 브라우저는 푸시 알림을 지원하지 않습니다.');
      }

      // Firebase 설정 확인
      if (!messaging || !VAPID_KEY) {
        throw new Error('Firebase 설정이 올바르지 않습니다.');
      }

      // 권한 요청
      const permission = await this.requestPermission();

      if (permission !== 'granted') {
        throw new Error('알림 권한이 거부되었습니다.');
      }

      // 서비스 워커 등록 확인
      const registration = await this.ensureServiceWorkerRegistration();

      // 기존 토큰 확인
      const existingToken = getStoredFCMToken();

      let token = existingToken;

      if (!existingToken) {
        token = await getToken(messaging, {
          vapidKey: VAPID_KEY,
          serviceWorkerRegistration: registration,
        });

        if (!token) {
          throw new Error('FCM 토큰 생성에 실패했습니다.');
        }

        await postFCMToken(token);

        setStoredFCMToken(token);
      }

      setStoredNotificationState(true);

      const result = {
        success: true,
        message: '알림이 성공적으로 활성화되었습니다.',
        data: { token },
      };

      return result;
    } catch (error) {
      setStoredNotificationState(false);

      throw new Error(createNotificationErrorMessage(error));
    }
  }

  static disable(): NotificationServiceResult {
    setStoredNotificationState(false);

    const result = {
      success: true,
      message: '알림이 비활성화되었습니다.',
    };

    return result;
  }

  static removeToken(): void {
    try {
      const existingToken = getStoredFCMToken();
      if (existingToken) {
        clearStoredFCMToken();
      }
      setStoredNotificationState(false);
    } catch (error) {
      console.error('[NotificationService] removeToken 오류:', error);
    }
  }

  static async getCurrentSettings(): Promise<boolean> {
    try {
      const response = await getNotificationSettings();

      const alertsOn = response.data.alertsOn;

      return alertsOn;
    } catch (error) {
      console.error('[NotificationService] getCurrentSettings 오류:', error);
      return false;
    }
  }

  private static async requestPermission(): Promise<NotificationPermission> {
    if (!('Notification' in window)) {
      throw new Error('이 브라우저는 알림을 지원하지 않습니다.');
    }

    const currentPermission = Notification.permission;

    if (currentPermission === 'granted') {
      return currentPermission;
    }

    const permission = await Notification.requestPermission();

    return permission;
  }

  private static async ensureServiceWorkerRegistration(): Promise<ServiceWorkerRegistration> {
    if (!('serviceWorker' in navigator)) {
      throw new Error('이 브라우저는 Service Worker를 지원하지 않습니다.');
    }

    const existingRegistration =
      await navigator.serviceWorker.getRegistration();

    if (existingRegistration) {
      return existingRegistration;
    }

    const registration =
      await navigator.serviceWorker.register('/service-worker.js');

    await navigator.serviceWorker.ready;

    return registration;
  }

  static getCurrentPermission(): NotificationPermission {
    if (typeof window === 'undefined' || !('Notification' in window)) {
      return 'default';
    }

    const permission = Notification.permission;
    return permission;
  }

  static checkIsSupported(): boolean {
    const notificationSupported = isNotificationSupported();
    const firebaseReady = !!messaging && !!VAPID_KEY;

    return notificationSupported && firebaseReady;
  }
}
