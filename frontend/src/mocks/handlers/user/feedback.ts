import { feedbackData } from '@/mocks/data/feedbackData';
import { BASE } from '@/mocks/handlers/constants';
import {
  findFeedbackById,
  notFoundResponse,
  successResponse,
} from '@/mocks/handlers/utils';
import { http, HttpResponse } from 'msw';

export const UserFeedbackHandlers = [
  http.get(`${BASE}/places/1/feedbacks`, ({ request }) => {
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

  http.post(`${BASE}/feedbacks/:feedbackId/like`, ({ params }) => {
    const feedback = findFeedbackById(Number(params.feedbackId));
    if (!feedback) return notFoundResponse;

    const before = feedback.likeCount;
    feedback.likeCount += 1;

    return successResponse({
      beforeLikeCount: before,
      afterLikeCount: feedback.likeCount,
    });
  }),

  http.delete(`${BASE}/feedbacks/:feedbackId/like`, ({ params }) => {
    const feedback = findFeedbackById(Number(params.feedbackId));
    if (!feedback) return notFoundResponse;

    const before = feedback.likeCount;
    feedback.likeCount = Math.max(0, before - 1);

    return successResponse({
      beforeLikeCount: before,
      afterLikeCount: feedback.likeCount,
    });
  }),
];
