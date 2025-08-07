import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export interface FeedbackType {
  feedbackId: number;
  content: string;
  status: FeedbackStatusType;
  isSecret: boolean;
  postedAt: string;
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
  postedAt: string; // ISO 형식 날짜 문자열
  userName: string;
}

export interface SuggestionFeedback {
  data: SuggestionFeedbackData;
  message: string;
  status: number;
}

export type FeedbackFilter = '전체' | '완료' | '미처리' | '나의글';

export type FeedbackFilterType = 'COMPLETED' | 'PENDING' | 'MINE' | 'ALL';

export type SortType = 'LATEST' | 'OLDEST' | 'LIKES';
