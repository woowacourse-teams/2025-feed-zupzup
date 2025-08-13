import { useState, useCallback } from 'react';
import { NotificationService } from '@/services';
import { useNotifications } from './useNotifications';
import { useNotificationState } from './useNotificationState';
import { useTestNotification } from './useTestNotification';

export const useNotificationSetting = (organizationId: number = 1) => {
  const { fcmStatus } = useNotifications();
  const { isEnabled, updateState } = useNotificationState(organizationId);
  const testNotification = useTestNotification(organizationId);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggle = useCallback(
    async (enabled: boolean) => {
      if (loading) return;

      setLoading(true);
      setError(null);

      try {
        if (enabled) {
          await NotificationService.enable(organizationId);
        } else {
          await NotificationService.disable(organizationId);
        }

        updateState(enabled);

        console.log(
          `[FCM] 알림이 ${enabled ? '활성화' : '비활성화'}되었습니다.`
        );
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '알림 설정 변경에 실패했습니다.';
        setError(errorMessage);

        updateState(!enabled);

        throw err;
      } finally {
        setLoading(false);
      }
    },
    [loading, organizationId, updateState]
  );

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    // 상태
    isToggleEnabled: isEnabled,
    isLoading: loading,
    error,
    fcmStatus,

    // 액션
    updateNotificationSetting: toggle,
    clearError,

    // 테스트 알림 (개발환경에서만)
    sendTestNotification:
      process.env.NODE_ENV === 'development'
        ? testNotification.sendTest
        : undefined,
  };
};
