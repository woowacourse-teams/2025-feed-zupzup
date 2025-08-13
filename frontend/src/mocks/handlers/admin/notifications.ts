import { http } from 'msw';
import { BASE } from '@/mocks/handlers/constants';
import {
  findNotificationSetting,
  updateNotificationSetting,
  addFCMToken,
  successResponse,
  errorResponse,
} from '@/mocks/handlers/utils/notificationUtils';

export const AdminNotificationHandlers = [
  http.post(`${BASE}/admin/notifications/tokens`, async ({ request }) => {
    try {
      const body = (await request.json()) as { token: string };

      if (!body.token) {
        return errorResponse('토큰이 필요합니다.', 400);
      }

      const tokenData = addFCMToken(body.token);

      return successResponse({
        tokenId: tokenData.tokenId,
        registeredAt: tokenData.registeredAt,
        message: 'FCM 토큰이 성공적으로 등록되었습니다.',
      });
    } catch (error) {
      console.error('[MSW] FCM 토큰 등록 에러:', error);
      return errorResponse('토큰 등록에 실패했습니다.', 500);
    }
  }),

  http.get(
    `${BASE}/admin/organizations/:organizationId/notifications`,
    ({ params }) => {
      const organizationId = Number(params.organizationId);
      const setting = findNotificationSetting(organizationId);

      if (!setting) {
        return successResponse({
          organizationId,
          notificationEnabled: false,
          fcmTokenRegistered: false,
        });
      }

      return successResponse(setting);
    }
  ),

  http.put(
    `${BASE}/admin/organizations/:organizationId/notifications`,
    async ({ params, request }) => {
      try {
        const organizationId = Number(params.organizationId);
        const body = (await request.json()) as { enabled: boolean };

        if (typeof body.enabled !== 'boolean') {
          return errorResponse('enabled 값이 필요합니다.', 400);
        }

        const updatedSetting = updateNotificationSetting(
          organizationId,
          body.enabled
        );

        return successResponse(updatedSetting);
      } catch (error) {
        console.error('[MSW] 알림 설정 변경 에러:', error);
        return errorResponse('알림 설정 변경에 실패했습니다.', 500);
      }
    }
  ),

  http.post(
    `${BASE}/admin/organizations/:organizationId/notifications/test`,
    ({ params }) => {
      const organizationId = Number(params.organizationId);
      const setting = findNotificationSetting(organizationId);

      if (!setting?.notificationEnabled) {
        return errorResponse('알림이 비활성화되어 있습니다.', 400);
      }

      if (!setting.fcmTokenRegistered) {
        return errorResponse('FCM 토큰이 등록되지 않았습니다.', 400);
      }

      return successResponse({
        sentAt: new Date().toISOString(),
        recipientCount: 1,
        notificationType: 'test',
        organizationId,
        message: '테스트 알림이 발송되었습니다.',
      });
    }
  ),
];
