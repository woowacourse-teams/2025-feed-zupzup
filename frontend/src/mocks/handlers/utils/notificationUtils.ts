import {
  notificationSettingsData,
  fcmTokensData,
} from '@/mocks/data/notificationData';
import { HttpResponse } from 'msw';

export const findNotificationSetting = (organizationId: number) => {
  return notificationSettingsData[organizationId];
};

export const updateNotificationSetting = (
  organizationId: number,
  enabled: boolean
) => {
  if (!notificationSettingsData[organizationId]) {
    notificationSettingsData[organizationId] = {
      organizationId,
      notificationEnabled: enabled,
      fcmTokenRegistered: false,
      updatedAt: new Date().toISOString(),
    };
  } else {
    notificationSettingsData[organizationId].notificationEnabled = enabled;
    notificationSettingsData[organizationId].updatedAt =
      new Date().toISOString();
  }
  return notificationSettingsData[organizationId];
};

export const addFCMToken = (token: string, organizationId: number = 1) => {
  const newToken = {
    tokenId: token,
    organizationId,
    registeredAt: new Date().toISOString(),
  };

  const existingIndex = fcmTokensData.findIndex(
    (t) => t.organizationId === organizationId
  );
  if (existingIndex !== -1) {
    fcmTokensData[existingIndex] = newToken;
  } else {
    fcmTokensData.push(newToken);
  }

  if (notificationSettingsData[organizationId]) {
    notificationSettingsData[organizationId].fcmTokenRegistered = true;
  }

  return newToken;
};

export const successResponse = (data?: unknown) => {
  return HttpResponse.json({
    data,
    status: 200,
    message: 'OK',
  });
};

export const errorResponse = (message: string, status: number = 400) => {
  return HttpResponse.json(
    {
      data: null,
      status,
      message,
    },
    { status }
  );
};
