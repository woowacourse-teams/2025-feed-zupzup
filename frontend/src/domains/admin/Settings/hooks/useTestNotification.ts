import { useState, useCallback } from 'react';
import { sendTestNotification } from '@/apis/notifications.api';
import type { TestNotificationPayload } from '@/types/notification.types';

export const useTestNotification = (organizationId: number) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const sendTest = useCallback(
    async (payload?: Partial<TestNotificationPayload>) => {
      setLoading(true);
      setError(null);

      try {
        const response = await sendTestNotification(organizationId);

        if (process.env.NODE_ENV === 'development') {
          setTimeout(() => {
            showLocalTestNotification({
              organizationId,
              title: payload?.title || '🔔 테스트 알림',
              body: payload?.body || '10초 후에 발송된 테스트 알림입니다!',
              icon: payload?.icon || '/favicon.ico',
            });
          }, 10000);
        }

        console.log('[TestNotification] 발송 완료:', response);
        return response;
      } catch (error) {
        const errorMessage =
          error instanceof Error
            ? error.message
            : '테스트 알림 발송에 실패했습니다.';
        setError(errorMessage);
        console.error('[TestNotification] 발송 실패:', error);
        throw error;
      } finally {
        setLoading(false);
      }
    },
    [organizationId]
  );

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    sendTest,
    loading,
    error,
    clearError,
  };
};

const showLocalTestNotification = (payload: TestNotificationPayload) => {
  if (
    typeof window !== 'undefined' &&
    'Notification' in window &&
    Notification.permission === 'granted'
  ) {
    new Notification(payload.title || '테스트 알림', {
      body: payload.body || '테스트 알림 내용',
      icon: payload.icon || '/favicon.ico',
      badge: '/favicon.ico',
      tag: `test-notification-${payload.organizationId}`,
      requireInteraction: true,
      data: {
        organizationId: payload.organizationId,
        type: 'test',
        timestamp: new Date().toISOString(),
      },
    });
  }
};
