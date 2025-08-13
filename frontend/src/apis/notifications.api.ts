import { apiClient } from './apiClient';
import type {
  FCMTokenRequest,
  NotificationSettingRequest,
} from '@/types/notification.types';

export interface ApiResponse<T = null> {
  data: T;
  status: number;
  message: string;
}

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

export const sendTestNotification = async (
  organizationId: number
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse, object>(
    `/admin/organizations/${organizationId}/notifications/test`,
    {}
  );

  return response as ApiResponse;
};
