import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

import type {
  FCMTokenRequest,
  NotificationSettingRequest,
} from '@/types/notification.types';

export const registerFCMToken = async (token: string): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse, FCMTokenRequest>(
    '/admin/notifications/tokens',
    { token }
  );
  return response as ApiResponse;
};

interface UpdateNotificationSettingsParams {
  organizationId: string;
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
