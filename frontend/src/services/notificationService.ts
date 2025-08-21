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
    console.log('[NotificationService] 알림 활성화 시작...');

    try {
      // 지원 여부 확인
      const supportCheck = isNotificationSupported();
      console.log('[NotificationService] 알림 지원 여부:', supportCheck);

      if (!supportCheck) {
        throw new Error('이 브라우저는 푸시 알림을 지원하지 않습니다.');
      }

      // Firebase 설정 확인
      console.log('[NotificationService] Firebase 설정 확인:', {
        messaging: !!messaging,
        vapidKey: !!VAPID_KEY,
        vapidKeyPrefix: VAPID_KEY
          ? VAPID_KEY.substring(0, 10) + '...'
          : 'undefined',
      });

      if (!messaging || !VAPID_KEY) {
        throw new Error('Firebase 설정이 올바르지 않습니다.');
      }

      // 권한 요청
      console.log('[NotificationService] 알림 권한 요청 중...');
      const permission = await this.requestPermission();
      console.log('[NotificationService] 권한 요청 결과:', permission);

      if (permission !== 'granted') {
        throw new Error('알림 권한이 거부되었습니다.');
      }

      // 서비스 워커 등록 확인
      console.log('[NotificationService] 서비스 워커 등록 확인 중...');
      const registration = await this.ensureServiceWorkerRegistration();
      console.log(
        '[NotificationService] 서비스 워커 등록 완료:',
        registration.scope
      );

      // 기존 토큰 확인
      const existingToken = getStoredFCMToken();
      console.log(
        '[NotificationService] 기존 토큰 존재 여부:',
        !!existingToken
      );
      console.log(
        '[NotificationService] 기존 토큰 타입:',
        typeof existingToken
      );
      if (existingToken && typeof existingToken === 'string') {
        console.log(
          '[NotificationService] 기존 토큰 (일부):',
          existingToken.substring(0, 20) + '...'
        );
      } else if (existingToken) {
        console.log('[NotificationService] 기존 토큰 (전체):', existingToken);
      }

      let token = existingToken;

      if (!existingToken) {
        console.log('[NotificationService] 새 FCM 토큰 생성 중...');
        token = await getToken(messaging, {
          vapidKey: VAPID_KEY,
          serviceWorkerRegistration: registration,
        });

        if (!token) {
          console.error('[NotificationService] FCM 토큰 생성 실패');
          throw new Error('FCM 토큰 생성에 실패했습니다.');
        }

        console.log(
          '[NotificationService] 새 토큰 생성 성공 (일부):',
          typeof token === 'string' ? token.substring(0, 20) + '...' : token
        );
        console.log('[NotificationService] 서버에 토큰 전송 중...');

        await postFCMToken(token);
        console.log('[NotificationService] 서버 토큰 전송 완료');

        setStoredFCMToken(token);
        console.log('[NotificationService] 로컬 스토리지에 토큰 저장 완료');
      } else {
        console.log('[NotificationService] 기존 토큰 사용');
      }

      setStoredNotificationState(true);
      console.log('[NotificationService] 알림 상태를 활성화로 설정');

      const result = {
        success: true,
        message: '알림이 성공적으로 활성화되었습니다.',
        data: { token },
      };

      console.log('[NotificationService] 알림 활성화 완료:', result);
      return result;
    } catch (error) {
      console.error('[NotificationService] 활성화 실패:', error);
      console.error(
        '[NotificationService] 에러 스택:',
        error instanceof Error ? error.stack : '스택 정보 없음'
      );

      setStoredNotificationState(false);
      console.log(
        '[NotificationService] 알림 상태를 비활성화로 설정 (에러로 인해)'
      );

      throw new Error(createNotificationErrorMessage(error));
    }
  }

  static disable(): NotificationServiceResult {
    console.log('[NotificationService] 알림 비활성화 시작...');

    setStoredNotificationState(false);
    console.log('[NotificationService] 알림 상태를 비활성화로 설정');

    const result = {
      success: true,
      message: '알림이 비활성화되었습니다.',
    };

    console.log('[NotificationService] 알림 비활성화 완료:', result);
    return result;
  }

  static removeToken(): void {
    console.log('[NotificationService] 토큰 삭제 시작...');

    try {
      const existingToken = getStoredFCMToken();
      console.log(
        '[NotificationService] 삭제할 토큰 존재 여부:',
        !!existingToken
      );

      clearStoredFCMToken();
      setStoredNotificationState(false);

      console.log('[NotificationService] 토큰 삭제 및 알림 상태 비활성화 완료');
    } catch (error) {
      console.error('[NotificationService] 토큰 삭제 실패:', error);
    }
  }

  static async getCurrentSettings(): Promise<boolean> {
    console.log('[NotificationService] 현재 설정 조회 시작...');

    try {
      const response = await getNotificationSettings();
      console.log('[NotificationService] 서버에서 받은 설정:', response.data);

      const alertsOn = response.data.alertsOn;
      console.log('[NotificationService] 현재 알림 설정:', alertsOn);

      return alertsOn;
    } catch (error) {
      console.error('[NotificationService] 설정 조회 실패:', error);
      return false;
    }
  }

  private static async requestPermission(): Promise<NotificationPermission> {
    console.log('[NotificationService] 권한 요청 시작...');

    if (!('Notification' in window)) {
      console.error(
        '[NotificationService] 브라우저가 Notification API를 지원하지 않음'
      );
      throw new Error('이 브라우저는 알림을 지원하지 않습니다.');
    }

    const currentPermission = Notification.permission;
    console.log('[NotificationService] 현재 권한 상태:', currentPermission);

    if (currentPermission === 'granted') {
      console.log('[NotificationService] 이미 권한이 허용된 상태');
      return currentPermission;
    }

    console.log('[NotificationService] 사용자에게 권한 요청 중...');
    const permission = await Notification.requestPermission();
    console.log('[NotificationService] 권한 요청 결과:', permission);

    return permission;
  }

  private static async ensureServiceWorkerRegistration(): Promise<ServiceWorkerRegistration> {
    console.log('[NotificationService] 서비스 워커 등록 확인 시작...');

    if (!('serviceWorker' in navigator)) {
      console.error(
        '[NotificationService] 브라우저가 Service Worker를 지원하지 않음'
      );
      throw new Error('이 브라우저는 Service Worker를 지원하지 않습니다.');
    }

    const existingRegistration =
      await navigator.serviceWorker.getRegistration();

    if (existingRegistration) {
      console.log(
        '[NotificationService] 기존 서비스 워커 등록 발견:',
        existingRegistration.scope
      );
      console.log(
        '[NotificationService] 서비스 워커 상태:',
        existingRegistration.active?.state
      );
      return existingRegistration;
    }

    console.log('[NotificationService] 새 서비스 워커 등록 중...');
    const registration =
      await navigator.serviceWorker.register('/service-worker.js');
    console.log(
      '[NotificationService] 서비스 워커 등록 완료:',
      registration.scope
    );

    console.log('[NotificationService] 서비스 워커 준비 대기 중...');
    await navigator.serviceWorker.ready;
    console.log('[NotificationService] 서비스 워커 준비 완료');

    return registration;
  }

  static getCurrentPermission(): NotificationPermission {
    if (typeof window === 'undefined' || !('Notification' in window)) {
      console.log(
        '[NotificationService] 브라우저 환경이 아니거나 Notification API 미지원'
      );
      return 'default';
    }

    const permission = Notification.permission;
    console.log('[NotificationService] 현재 권한 상태 조회:', permission);
    return permission;
  }

  static checkIsSupported(): boolean {
    const notificationSupported = isNotificationSupported();
    const firebaseReady = !!messaging && !!VAPID_KEY;

    console.log('[NotificationService] 지원 여부 확인:', {
      알림지원: notificationSupported,
      Firebase준비: firebaseReady,
      전체지원: notificationSupported && firebaseReady,
    });

    return notificationSupported && firebaseReady;
  }
}
