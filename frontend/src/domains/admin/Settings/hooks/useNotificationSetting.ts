import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNotifications } from './useNotifications';
import { useUpdateNotificationSetting } from './useUpdateNotificationSetting';
import { getNotificationSettings } from '@/apis/notifications.api';
import { QUERY_KEYS } from '@/constants/queryKeys';

export const useNotificationSetting = () => {
  const {
    fcmStatus,
    isEnabled: localEnabled,
    updateState,
  } = useNotifications();

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

  const updateMutation = useUpdateNotificationSetting({
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
