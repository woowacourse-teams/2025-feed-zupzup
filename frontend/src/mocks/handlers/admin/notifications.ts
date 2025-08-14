import { http, HttpResponse } from 'msw';
import { BASE } from '@/mocks/handlers/constants';

export const AdminNotificationHandlers = [
  http.post(`${BASE}/admin/notifications/tokens`, () => {
    return HttpResponse.json({
      data: {
        tokenId: 'mock-token-' + Date.now(),
        registeredAt: new Date().toISOString(),
        message: 'FCM 토큰이 성공적으로 등록되었습니다.',
      },
      status: 200,
      message: 'OK',
    });
  }),

  http.get(
    `${BASE}/admin/organizations/:organizationId/notifications`,
    ({ params }) => {
      return HttpResponse.json({
        data: {
          organizationId: Number(params.organizationId),
          notificationEnabled: true,
          fcmTokenRegistered: true,
        },
        status: 200,
        message: 'OK',
      });
    }
  ),

  http.put(
    `${BASE}/admin/organizations/:organizationId/notifications`,
    async ({ params, request }) => {
      const body = (await request.json()) as { enabled: boolean };

      return HttpResponse.json({
        data: {
          organizationId: Number(params.organizationId),
          notificationEnabled: body.enabled,
          updatedAt: new Date().toISOString(),
        },
        status: 200,
        message: 'OK',
      });
    }
  ),

  http.post(
    `${BASE}/admin/organizations/:organizationId/notifications/test`,
    ({ params }) => {
      return HttpResponse.json({
        data: {
          sentAt: new Date().toISOString(),
          recipientCount: 1,
          notificationType: 'test',
          organizationId: Number(params.organizationId),
          message: '테스트 알림이 발송되었습니다.',
        },
        status: 200,
        message: 'OK',
      });
    }
  ),
];
