import { apiClient } from '@/apis/apiClient';
import type {
  FCMTokenRequest,
  NotificationSettingRequest,
  ApiResponse,
} from '@/types/notification.types';

interface RegisterFCMTokenParams {
  token: string;
  onSuccess: (data: ApiResponse) => void;
  onError: () => void;
}

interface UpdateNotificationSettingsParams {
  organizationId: number;
  enabled: boolean;
  onSuccess: (data: ApiResponse) => void;
  onError: () => void;
}

export async function registerFCMToken({
  token,
  onSuccess,
  onError,
}: RegisterFCMTokenParams) {
  await apiClient.post<ApiResponse, FCMTokenRequest>(
    '/admin/notifications/tokens',
    { token },
    { onError, onSuccess }
  );
}

export async function updateNotificationSettings({
  organizationId,
  enabled,
  onSuccess,
  onError,
}: UpdateNotificationSettingsParams) {
  await apiClient.put<ApiResponse, NotificationSettingRequest>(
    `/admin/organizations/${organizationId}/notifications`,
    { enabled },
    { onError, onSuccess }
  );
}
