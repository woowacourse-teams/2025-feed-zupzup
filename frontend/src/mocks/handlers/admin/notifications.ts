import { http, HttpResponse } from 'msw';

const API_BASE = process.env.BASE_URL;

export const AdminNotificationHandlers = [
  http.post(`${API_BASE}/admin/notifications/tokens`, async () => {
    try {
      return HttpResponse.json(
        {
          data: null,
          status: 200,
          message: 'FCM 토큰이 성공적으로 등록되었습니다.',
        },
        { status: 200 }
      );
    } catch (error) {
      console.error('❌ [MSW] FCM 토큰 등록 에러:', error);
      return HttpResponse.json(
        {
          data: null,
          status: 200,
          message: '처리 완료',
        },
        { status: 200 }
      );
    }
  }),

  http.put(
    `${API_BASE}/admin/organizations/:id/notifications`,
    async ({ params, request }) => {
      try {
        const { enabled } = (await request.json()) as { enabled: boolean };
        return HttpResponse.json(
          {
            data: {
              organizationId: Number(params.id),
              notificationEnabled: enabled,
              updatedAt: new Date().toISOString(),
            },
            status: 200,
            message: 'OK',
          },
          { status: 200 }
        );
      } catch (error) {
        console.error('❌ [MSW] 알림 설정 변경 에러:', error);
        return HttpResponse.json(
          {
            data: null,
            status: 200,
            message: '처리 완료',
          },
          { status: 200 }
        );
      }
    }
  ),

  http.get(
    `${API_BASE}/admin/organizations/:id/notifications`,
    async ({ params }) => {
      return HttpResponse.json(
        {
          data: {
            organizationId: Number(params.id),
            notificationEnabled: true,
            fcmTokenRegistered: true,
          },
          status: 200,
          message: 'OK',
        },
        { status: 200 }
      );
    }
  ),

  http.post(
    `${API_BASE}/admin/organizations/:id/notifications/test`,
    async ({ params }) => {
      if (
        'serviceWorker' in navigator &&
        Notification.permission === 'granted'
      ) {
        navigator.serviceWorker.ready.then((registration) => {
          registration.showNotification('🔔 테스트 알림', {
            body: `조직 ${params.id}에서 발송된 테스트 알림입니다.`,
            icon: '/logo192.png',
            tag: 'test-notification',
            data: {
              url: '/admin/settings',
              organizationId: params.id,
              type: 'test',
            },
            requireInteraction: true,
          });
        });
      }

      return HttpResponse.json(
        {
          data: {
            sentAt: new Date().toISOString(),
            recipientCount: 1,
            notificationType: 'test',
          },
          status: 200,
          message: 'OK',
        },
        { status: 200 }
      );
    }
  ),

  http.all(`${API_BASE}/*`, ({ request }) => {
    console.log('🔍 [MSW] API 서버 요청 감지:', request.method, request.url);
  }),
];
