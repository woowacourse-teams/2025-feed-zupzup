import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export interface UserFeedback {
  feedbackId: number;
  content: string;
  status: FeedbackStatusType;
  isSecret: boolean;
  isLiked: boolean;
  createdAt: string; // ISO 형식 날짜 문자열
  userName: string;
}

export interface AdminFeedback extends Omit<UserFeedback, 'isLiked'> {
  imgUrl: string | null;
  likeCount: number;
}

export interface FeedbackResponse<T> {
  feedbacks: T[];
  hasNext: boolean;
  nextCursorId: number;
}
