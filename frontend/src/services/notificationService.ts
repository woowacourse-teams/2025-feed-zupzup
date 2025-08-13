import { getToken } from 'firebase/messaging';
import { messaging } from '@/firebase/messaging';
import { VAPID_KEY } from '@/firebase/config';
import {
  registerFCMToken,
  updateNotificationSettings,
} from '@/apis/notifications.api';
import {
  setStoredNotificationState,
  isNotificationSupported,
  createNotificationErrorMessage,
} from '@/utils/notificationUtils';
import type { NotificationServiceResult } from '@/types/notification.types';

export class NotificationService {
  static async enable(
    organizationId: number
  ): Promise<NotificationServiceResult> {
    try {
      if (!isNotificationSupported()) {
        throw new Error('이 브라우저는 푸시 알림을 지원하지 않습니다.');
      }

      if (!messaging || !VAPID_KEY) {
        throw new Error('Firebase 설정이 올바르지 않습니다.');
      }

      const permission = await this.requestPermission();
      if (permission !== 'granted') {
        throw new Error('알림 권한이 거부되었습니다.');
      }

      const registration = await this.ensureServiceWorkerRegistration();

      const token = await getToken(messaging, {
        vapidKey: VAPID_KEY,
        serviceWorkerRegistration: registration,
      });

      if (!token) {
        throw new Error('FCM 토큰 생성에 실패했습니다.');
      }

      await registerFCMToken(token);

      await updateNotificationSettings(organizationId, true);

      setStoredNotificationState(organizationId, true);

      return {
        success: true,
        message: '알림이 성공적으로 활성화되었습니다.',
        data: { token, organizationId },
      };
    } catch (error) {
      console.error('[NotificationService] 활성화 실패:', error);

      setStoredNotificationState(organizationId, false);

      throw new Error(createNotificationErrorMessage(error));
    }
  }

  static async disable(
    organizationId: number
  ): Promise<NotificationServiceResult> {
    try {
      await updateNotificationSettings(organizationId, false);

      setStoredNotificationState(organizationId, false);

      return {
        success: true,
        message: '알림이 비활성화되었습니다.',
        data: { organizationId },
      };
    } catch (error) {
      console.error('[NotificationService] 비활성화 실패:', error);

      setStoredNotificationState(organizationId, true);

      throw new Error('알림 비활성화에 실패했습니다.');
    }
  }

  private static async requestPermission(): Promise<NotificationPermission> {
    if (!('Notification' in window)) {
      throw new Error('이 브라우저는 알림을 지원하지 않습니다.');
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
    return Notification.permission;
  }

  static isSupported(): boolean {
    return isNotificationSupported() && !!messaging && !!VAPID_KEY;
  }
}
