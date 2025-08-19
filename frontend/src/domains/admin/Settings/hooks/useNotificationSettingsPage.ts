import { useEffect, useState, useRef } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useFCMManager } from './useFCMManager';
import { useNotificationSettingMutation } from './useNotificationSettingMutation';
import { getNotificationSettings } from '@/apis/notifications.api';
import { QUERY_KEYS } from '@/constants/queryKeys';

export const useNotificationSettingsPage = () => {
  const { fcmStatus, isEnabled: localEnabled, updateState } = useFCMManager();

  const [pendingState, setPendingState] = useState<boolean | null>(null);
  const originalStateRef = useRef<boolean | null>(null);

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

  const actualToggleState = alertsOn ?? localEnabled;

  const updateMutation = useNotificationSettingMutation({
    localEnabled,
    updateState,
    onErrorCallback: () => {
      setPendingState(null);
      originalStateRef.current = null;
    },
    onSuccessCallback: () => {
      setPendingState(null);
      originalStateRef.current = null;
    },
  });

  const isToggleEnabled = updateMutation.isPending
    ? (pendingState ?? originalStateRef.current ?? actualToggleState)
    : actualToggleState;

  const updateNotificationSetting = (enabled: boolean) => {
    if (enabled === actualToggleState) {
      return;
    }

    if (updateMutation.isPending) {
      return;
    }

    originalStateRef.current = actualToggleState;
    setPendingState(enabled);

    updateMutation.mutate({ enabled });
  };

  return {
    isToggleEnabled,
    isLoading: isQueryLoading || updateMutation.isPending,
    fcmStatus,
    updateNotificationSetting,
  };
};
