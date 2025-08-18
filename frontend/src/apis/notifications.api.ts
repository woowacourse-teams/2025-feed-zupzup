import { apiClient } from '@/apis/apiClient';
import type {
  FCMTokenRequest,
  NotificationSettingRequest,
  NotificationSettingsResponse,
  ApiResponse,
} from '@/types/notification.types';

export const registerFCMToken = async (
  notificationToken: string
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse, FCMTokenRequest>(
    '/admin/notifications/token',
    { notificationToken }
  );
  return response as ApiResponse;
};

export const getNotificationSettings =
  async (): Promise<NotificationSettingsResponse> => {
    const response = await apiClient.get<NotificationSettingsResponse>(
      '/admin/notifications/settings'
    );
    return response as NotificationSettingsResponse;
  };

interface PatchNotificationSettingsParams {
  alertsOn: boolean;
}

export const patchNotificationSettings = async ({
  alertsOn,
}: PatchNotificationSettingsParams): Promise<ApiResponse> => {
  const response = await apiClient.patch<
    ApiResponse,
    NotificationSettingRequest
  >('/admin/notifications/settings', { alertsOn });
  return response as ApiResponse;
};
