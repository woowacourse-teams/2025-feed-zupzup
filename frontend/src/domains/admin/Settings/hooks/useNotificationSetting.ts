// hooks/useNotificationSetting.ts (기존 구조 유지)
import { useState, useCallback, useEffect } from 'react';
import { NotificationService } from '@/services/notificationService';
import { useNotifications } from './useNotifications';
import {
  getNotificationSettings,
  updateNotificationSettings,
} from '@/apis/notifications.api';

export const useNotificationSetting = () => {
  const {
    fcmStatus,
    isEnabled: localEnabled,
    updateState,
  } = useNotifications();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [serverEnabled, setServerEnabled] = useState<boolean | null>(null);

  useEffect(() => {
    const fetchServerState = async () => {
      try {
        const response = await getNotificationSettings(3);
        setServerEnabled(response.data.alertsOn);

        if (response.data.alertsOn !== localEnabled) {
          updateState(response.data.alertsOn);
        }
      } catch (error) {
        console.warn('서버 상태 조회 실패, 로컬 상태 사용:', error);
      }
    };

    fetchServerState();
  }, []);

  const isToggleEnabled = serverEnabled !== null ? serverEnabled : localEnabled;

  const toggle = useCallback(
    async (enabled: boolean) => {
      if (loading) return;

      setLoading(true);
      setError(null);

      try {
        if (enabled) {
          await NotificationService.enable();
        } else {
          await NotificationService.disable();
        }

        await updateNotificationSettings({ alertsOn: enabled });

        updateState(enabled);
        setServerEnabled(enabled);

        console.log(`알림이 ${enabled ? '활성화' : '비활성화'}되었습니다.`);
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '알림 설정 변경에 실패했습니다.';
        setError(errorMessage);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [loading, updateState]
  );

  return {
    isToggleEnabled,
    isLoading: loading,
    error,
    fcmStatus,
    updateNotificationSetting: toggle,
    clearError: () => setError(null),
  };
};
