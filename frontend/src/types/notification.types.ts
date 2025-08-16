export interface ApiResponse<T = null> {
  data: T;
  status: number;
  message: string;
}

export interface FCMTokenRequest {
  notificationToken: string;
}

export interface NotificationSettingRequest {
  alertsOn: boolean;
}

export type NotificationSettingsResponse = ApiResponse<{
  alertsOn: boolean;
}>;

export interface NotificationSetting {
  alertsOn: boolean;
  fcmTokenRegistered: boolean;
  updatedAt?: string;
}

export interface FCMStatus {
  isSupported: boolean;
  permission: NotificationPermission;
  hasToken: boolean;
  error?: string | null;
}

export interface NotificationServiceResult {
  success: boolean;
  message?: string;
  data?: unknown;
}
