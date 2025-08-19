import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useFCMManager } from './useFCMManager';
import { useNotificationSettingMutation } from './useNotificationSettingMutation';
import { getNotificationSettings } from '@/apis/notifications.api';
import { QUERY_KEYS } from '@/constants/queryKeys';

export const useNotificationSettingsPage = () => {
  const { fcmStatus, isEnabled: localEnabled, updateState } = useFCMManager();

  const { data: serverSettings, isLoading: isQueryLoading } = useQuery({
    queryKey: QUERY_KEYS.notificationSettings(),
    queryFn: getNotificationSettings,
  });

  const alertsOn = serverSettings?.data?.alertsOn;

  useEffect(() => {
    if (alertsOn !== undefined && alertsOn !== localEnabled) {
      updateState(alertsOn);
    }
  }, [alertsOn, localEnabled, updateState]);

  const isToggleEnabled = alertsOn ?? localEnabled;

  const updateMutation = useNotificationSettingMutation({
    localEnabled,
    updateState,
  });

  const updateNotificationSetting = async (enabled: boolean) => {
    if (enabled === isToggleEnabled) {
      return;
    }

    if (updateMutation.isPending) {
      return;
    }

    await updateMutation.mutateAsync({ enabled });
  };

  return {
    isToggleEnabled,
    isLoading: isQueryLoading || updateMutation.isPending,
    fcmStatus,
    updateNotificationSetting,
  };
};
