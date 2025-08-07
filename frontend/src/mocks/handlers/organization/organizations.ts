import { BASE } from '@/mocks/handlers/constants';
import { http, HttpResponse } from 'msw';

export const OrganizationHandler = [
  // 조직 이름 조회
  http.get(`${BASE}/organizations/1`, () => {
    return HttpResponse.json({
      data: {
        organizationName: '우아한테크코스',
        totalCheeringCount: 1000,
      },
    });
  }),
];
