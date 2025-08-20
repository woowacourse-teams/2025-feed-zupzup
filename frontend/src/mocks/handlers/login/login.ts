import { BASE } from '@/mocks/handlers/constants';
import { http, HttpResponse } from 'msw';

export const AdminHandler = [
  http.post(`${BASE}/admin/login`, async ({ request }) => {
    const body = await request.json();
    const { loginId } = body as { loginId: string; password: string };

    return HttpResponse.json(
      {
        data: {
          loginId: loginId,
          adminName: '관리자',
          adminId: 1,
        },
        status: 200,
        message: 'OK',
      },
      { status: 200 }
    );
  }),
];
