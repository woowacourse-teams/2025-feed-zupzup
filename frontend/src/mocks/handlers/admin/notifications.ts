import { http, HttpResponse } from 'msw';
import { BASE } from '@/mocks/handlers/constants';

export const AdminNotificationHandlers = [
  http.post(`${BASE}/admin/notifications/token`, async ({ request }) => {
    const body = (await request.json()) as { notificationToken: string };

    return HttpResponse.json({
      data: {
        tokenId: 'mock-token-' + Date.now(),
        notificationToken: body.notificationToken,
        registeredAt: new Date().toISOString(),
      },
      status: 200,
      message: 'OK',
    });
  }),

  http.get(`${BASE}/admin/notifications/settings`, () => {
    return HttpResponse.json({
      data: {
        alertsOn: true,
      },
      status: 200,
      message: 'OK',
    });
  }),

  http.patch(`${BASE}/admin/notifications/settings`, async ({ request }) => {
    const body = (await request.json()) as { alertsOn: boolean };

    return HttpResponse.json({
      data: {
        alertsOn: body.alertsOn,
        updatedAt: new Date().toISOString(),
      },
      status: 200,
      message: 'OK',
    });
  }),

  http.post(`${BASE}/admin/notifications/test`, async ({ request }) => {
    const body = (await request.json()) as {
      title?: string;
      body?: string;
      icon?: string;
    };

    return HttpResponse.json({
      data: {
        sentAt: new Date().toISOString(),
        recipientCount: 1,
        notificationType: 'test',
        title: body.title || '테스트 알림',
        body: body.body || '테스트 메시지입니다.',
      },
      status: 200,
      message: 'OK',
    });
  }),
];
