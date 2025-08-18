import { useEffect } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { NotificationService } from '@/services/notificationService';
import { useNotifications } from './useNotifications';
import {
  getNotificationSettings,
  patchNotificationSettings,
} from '@/apis/notifications.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { NotificationSettingsResponse } from '@/types/notification.types';

interface UpdateNotificationSettingParams {
  enabled: boolean;
}

export const useNotificationSetting = () => {
  const queryClient = useQueryClient();
  const {
    fcmStatus,
    isEnabled: localEnabled,
    updateState,
  } = useNotifications();
  const { showErrorModal } = useErrorModalContext();

  const { data: serverSettings, isLoading: isQueryLoading } = useQuery({
    queryKey: QUERY_KEYS.notificationSettings(),
    queryFn: getNotificationSettings,
  });

  const alertsOn = serverSettings?.data?.alertsOn;

  useEffect(() => {
    if (alertsOn !== undefined && alertsOn !== localEnabled) {
      updateState(alertsOn);
    }
  }, [alertsOn, localEnabled]);

  const isToggleEnabled = alertsOn ?? localEnabled;

  const updateMutation = useMutation({
    mutationFn: async ({ enabled }: UpdateNotificationSettingParams) => {
      if (enabled) {
        await NotificationService.enable();
      } else {
        await NotificationService.disable();
      }

      return patchNotificationSettings({ alertsOn: enabled });
    },
    onMutate: async ({ enabled }: UpdateNotificationSettingParams) => {
      await queryClient.cancelQueries({
        queryKey: QUERY_KEYS.notificationSettings(),
      });

      const previousServerData = queryClient.getQueryData(
        QUERY_KEYS.notificationSettings()
      );
      const previousLocalState = localEnabled;

      queryClient.setQueryData(
        QUERY_KEYS.notificationSettings(),
        (old: NotificationSettingsResponse) => ({
          ...old,
          data: { alertsOn: enabled },
        })
      );

      updateState(enabled);

      return { previousServerData, previousLocalState };
    },
    onError: (_, __, context) => {
      if (context?.previousServerData) {
        queryClient.setQueryData(
          QUERY_KEYS.notificationSettings(),
          context.previousServerData
        );
      }
      if (context?.previousLocalState !== undefined) {
        updateState(context.previousLocalState);
      }

      showErrorModal('알림 설정 변경에 실패했습니다.', '에러');
    },
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
