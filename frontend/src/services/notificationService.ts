import { getToken } from 'firebase/messaging';
import { messaging } from '@/firebase/messaging';
import { VAPID_KEY } from '@/firebase/config';
import {
  registerFCMToken,
  updateNotificationSettings,
  getNotificationSettings,
} from '@/apis/notifications.api';
import {
  setStoredNotificationState,
  isNotificationSupported,
  createNotificationErrorMessage,
} from '@/utils/notificationUtils';
import type { NotificationServiceResult } from '@/types/notification.types';

export class NotificationService {
  static async enable(adminId: number = 3): Promise<NotificationServiceResult> {
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

      // adminId 전달
      await updateNotificationSettings({
        alertsOn: true,
        adminId,
      });

      setStoredNotificationState(true);

      return {
        success: true,
        message: '알림이 성공적으로 활성화되었습니다.',
        data: { token, adminId },
      };
    } catch (error) {
      console.error('[NotificationService] 활성화 실패:', error);

      setStoredNotificationState(false);

      throw new Error(createNotificationErrorMessage(error));
    }
  }

  static async disable(
    adminId: number = 3
  ): Promise<NotificationServiceResult> {
    try {
      await updateNotificationSettings({
        alertsOn: false,
        adminId,
      });

      setStoredNotificationState(false);

      return {
        success: true,
        message: '알림이 비활성화되었습니다.',
        data: { adminId },
      };
    } catch (error) {
      console.error('[NotificationService] 비활성화 실패:', error);

      setStoredNotificationState(true);

      throw new Error('알림 비활성화에 실패했습니다.');
    }
  }

  static async getCurrentSettings(adminId: number = 3): Promise<boolean> {
    try {
      // adminId 전달
      const response = await getNotificationSettings(adminId);
      return response.data.alertsOn;
    } catch (error) {
      console.error('[NotificationService] 설정 조회 실패:', error);
      return false;
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

  static checkIsSupported(): boolean {
    return isNotificationSupported() && !!messaging && !!VAPID_KEY;
  }
}
