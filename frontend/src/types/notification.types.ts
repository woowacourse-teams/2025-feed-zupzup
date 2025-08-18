export interface FCMTokenRequest {
  token: string;
}

export interface NotificationSettingRequest {
  enabled: boolean;
}

export interface NotificationSetting {
  organizationId: number;
  notificationEnabled: boolean;
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

export interface TestNotificationPayload {
  organizationId: number;
  title?: string;
  body?: string;
  icon?: string;
}
