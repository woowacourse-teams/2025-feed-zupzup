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

export const getNotificationSettings = async (
  adminId: number = 3
): Promise<NotificationSettingsResponse> => {
  const response = await apiClient.get<NotificationSettingsResponse>(
    `/admin/notifications/settings?adminId=${adminId}`
  );
  return response as NotificationSettingsResponse;
};

interface UpdateNotificationSettingsParams {
  alertsOn: boolean;
  adminId?: number;
}

export const updateNotificationSettings = async ({
  alertsOn,
  adminId = 3,
}: UpdateNotificationSettingsParams): Promise<ApiResponse> => {
  const response = await apiClient.patch<
    ApiResponse,
    NotificationSettingRequest
  >(`/admin/notifications/settings?adminId=${adminId}`, { alertsOn });
  return response as ApiResponse;
};
