import { apiClient } from './apiClient';
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

export const updateNotificationSettings = async (
  organizationId: number,
  enabled: boolean
): Promise<ApiResponse> => {
  const response = await apiClient.put<ApiResponse, NotificationSettingRequest>(
    `/admin/organizations/${organizationId}/notifications`,
    { enabled }
  );

  return response as ApiResponse;
};
