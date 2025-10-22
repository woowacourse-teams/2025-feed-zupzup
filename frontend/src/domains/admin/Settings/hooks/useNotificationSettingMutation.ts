import { patchNotificationSettings } from '@/apis/notifications.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { NotificationService } from '@/services/notificationService';
import { NotificationSettingsResponse } from '@/types/notification.types';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface UpdateNotificationSettingParams {
  enabled: boolean;
}

interface UseNotificationSettingMutationProps {
  localEnabled: boolean;
  updateState: (enabled: boolean) => void;
}

export const useNotificationSettingMutation = ({
  localEnabled,
  updateState,
}: UseNotificationSettingMutationProps) => {
  const queryClient = useQueryClient();

  return useMutation({
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

      queryClient.setQueryData(
        QUERY_KEYS.notificationSettings(),
        (old: NotificationSettingsResponse | undefined) => ({
          ...old,
          data: { alertsOn: enabled },
        })
      );

      updateState(enabled);

      return { previousServerData, previousLocalState: localEnabled };
    },
    onError: (_, __, context) => {
      queryClient.removeQueries({
        queryKey: QUERY_KEYS.notificationSettings(),
      });

      if (context?.previousLocalState !== undefined) {
        updateState(context.previousLocalState);
      }
    },
  });
};
