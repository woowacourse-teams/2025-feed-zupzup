export interface Feedback {
  feedbackId: number;
  content: string;
  status: 'WAITING' | 'CONFIRMED';
  isSecret: boolean;
  isLiked: boolean;
  createdAt: string; // ISO 형식 날짜 문자열
}

export interface FeedbackResponse {
  feedbacks: Feedback[];
  hasNext: boolean;
  nextCursorId: number;
}
