import { feedbackData } from '@/mocks/data/feedbackData';
import { HttpResponse } from 'msw';

export const findFeedbackById = (id: number) =>
  feedbackData.find((item) => item.feedbackId === id);

export const notFoundResponse = HttpResponse.json(
  { status: 404, message: '피드백을 찾을 수 없습니다.' },
  { status: 404 }
);

export const successResponse = <T>(data: T) =>
  HttpResponse.json({
    data,
    status: 200,
    message: 'OK',
  });
