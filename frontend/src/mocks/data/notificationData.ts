export interface NotificationSettingType {
  organizationId: number;
  notificationEnabled: boolean;
  fcmTokenRegistered: boolean;
  updatedAt: string;
}

export interface FCMTokenType {
  tokenId: string;
  organizationId: number;
  registeredAt: string;
}

export const notificationSettingsData: Record<number, NotificationSettingType> =
  {
    1: {
      organizationId: 1,
      notificationEnabled: false,
      fcmTokenRegistered: false,
      updatedAt: '2024-07-01T12:00:00.000Z',
    },
  };

export const fcmTokensData: FCMTokenType[] = [];
