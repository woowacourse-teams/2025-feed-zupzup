import { apiClient } from '@/apis/apiClient';
import type {
  FCMTokenRequest,
  NotificationSettingRequest,
  ApiResponse,
} from '@/types/notification.types';

export const registerFCMToken = async (token: string): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse, FCMTokenRequest>(
    '/admin/notifications/tokens',
    { token }
  );
  return response as ApiResponse;
};

interface UpdateNotificationSettingsParams {
  organizationId: number;
  enabled: boolean;
}

export const updateNotificationSettings = async ({
  organizationId,
  enabled,
}: UpdateNotificationSettingsParams): Promise<ApiResponse> => {
  const response = await apiClient.put<ApiResponse, NotificationSettingRequest>(
    `/admin/organizations/${organizationId}/notifications`,
    { enabled }
  );

  return response as ApiResponse;
};
