import { feedbackData } from '@/mocks/data/feedbackData';
import { BASE } from '@/mocks/handlers/constants';
import {
  findFeedbackById,
  notFoundResponse,
  successResponse,
} from '@/mocks/handlers/utils';
import { http, HttpResponse } from 'msw';

export const AdminFeedbackHandlers = [
  // 피드백 조회
  http.get(`${BASE}/admin/organizations/1/feedbacks`, ({ request }) => {
    const url = new URL(request.url);
    const cursorId = url.searchParams.get('cursorId');

    if (!cursorId || cursorId === '0') {
      return HttpResponse.json({
        data: {
          feedbacks: feedbackData.slice(0, 5),
          hasNext: true,
          nextCursorId: 6,
        },
      });
    }

    if (cursorId === '6') {
      return HttpResponse.json({
        data: {
          feedbacks: feedbackData.slice(5, 10),
          hasNext: false,
          nextCursorId: 11,
        },
      });
    }

    return notFoundResponse;
  }),

  // 피드백 삭제
  http.delete(`${BASE}/admin/feedbacks/:feedbackId`, async ({ params }) => {
    const feedbackId = Number(params.feedbackId);

    const feedback = findFeedbackById(feedbackId);

    if (!feedback) return notFoundResponse;

    feedbackData.filter(
      (mockFeedback) => mockFeedback.feedbackId !== feedbackId
    );

    return successResponse(`feedbackId ${feedbackId} 삭제 완료`);
  }),

  // 피드백 상태 변경
  http.patch(
    `${BASE}/admin/feedbacks/:feedbackId/comment`,
    async ({ params, request }) => {
      const feedbackId = Number(params.feedbackId);
      const body = await request.json();
      const newStatus =
        typeof body === 'object' && body !== null && 'comment' in body
          ? ((body as Record<string, undefined>).comment ?? '확인했습니다.')
          : '확인했습니다.';

      const feedback = findFeedbackById(feedbackId);

      if (!feedback) return notFoundResponse;

      feedback.status = 'CONFIRMED';
      feedback.comment = newStatus;
      const modifiedAt = new Date().toISOString();

      return successResponse({
        feedbackId,
        status: newStatus,
        modifiedAt,
      });
    }
  ),
];
