import { FeedbackType } from '@/types/feedback.types';

export const FEEDBACK_MOCK: FeedbackType[] = [
  {
    userName: '김코딩',
    feedbackId: 1,
    status: 'WAITING',
    content: '다음에는 점심 메뉴를 다양하게 해주세요.',
    postedAt: '2024-06-01T10:00:00Z',
    isSecret: false,
    likeCount: 5,
    category: '기타',
  },
  {
    userName: '웨이브하다가허리부러진 웨이드',
    feedbackId: 2,
    status: 'WAITING',
    content: '회의실 예약 시스템이 불편해요.',
    postedAt: '2024-06-02T12:30:00Z',
    isSecret: true,
    likeCount: 2,
    category: '시설',
  },
  {
    userName: '웨이브하다가허리부러진 웨이드',
    feedbackId: 3,
    status: 'CONFIRMED',
    content: '에어컨 온도를 조금 높여주세요.',
    postedAt: '2024-06-03T09:15:00Z',
    isSecret: false,
    likeCount: 3,
    category: '시설',
  },
];
