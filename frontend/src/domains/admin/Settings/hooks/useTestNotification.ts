import { useState, useCallback } from 'react';
import { sendTestNotification } from '@/apis/notifications.api';

export const useTestNotification = (organizationId: number) => {
  const [loading, setLoading] = useState(false);

  const sendTest = useCallback(async () => {
    setLoading(true);

    try {
      const response = await sendTestNotification(organizationId);

      if (process.env.NODE_ENV === 'development') {
        setTimeout(() => {
          if (
            typeof window !== 'undefined' &&
            'Notification' in window &&
            Notification.permission === 'granted'
          ) {
            new Notification('🔔 테스트 알림', {
              body: '10초 후에 발송된 테스트 알림입니다!',
              icon: '/favicon.ico',
              tag: `test-notification-${organizationId}`,
              requireInteraction: true,
            });
          }
        }, 10000);

        if (typeof window !== 'undefined') {
          alert('10초 후에 테스트 알림이 발송됩니다! ⏰');
        }
      }

      console.log('[TestNotification] 발송 완료:', response);
      return response;
    } catch (error) {
      console.error('[TestNotification] 발송 실패:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  }, [organizationId]);

  return {
    sendTest,
    loading,
  };
};
