import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export interface UserFeedback {
  feedbackId: number;
  content: string;
  status: FeedbackStatusType;
  isSecret: boolean;
  createdAt: string;
  userName: string;
  likeCount: number;
}

export interface FeedbackResponse<T> {
  feedbacks: T[];
  hasNext: boolean;
  nextCursorId: number;
}

interface SuggestionFeedbackData {
  feedbackId: number;
  content: string;
  status: FeedbackStatusType;
  isSecret: boolean;
  createdAt: string; // ISO 형식 날짜 문자열
  userName: string;
}

export interface SuggestionFeedback {
  data: SuggestionFeedbackData;
  message: string;
  status: number;
}
