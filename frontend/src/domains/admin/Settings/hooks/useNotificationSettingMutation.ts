import { useMutation, useQueryClient } from '@tanstack/react-query';
import { NotificationService } from '@/services/notificationService';
import { patchNotificationSettings } from '@/apis/notifications.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { NotificationSettingsResponse } from '@/types/notification.types';

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
  const { showErrorModal } = useErrorModalContext();

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
};
