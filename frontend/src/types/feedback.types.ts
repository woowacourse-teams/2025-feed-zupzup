export interface UserFeedback {
  feedbackId: number;
  content: string;
  status: 'WAITING' | 'CONFIRMED';
  isSecret: boolean;
  isLiked: boolean;
  createdAt: string; // ISO 형식 날짜 문자열
  userName: string;
}

export interface AdminFeedback extends Omit<UserFeedback, 'isLiked'> {
  imgUrl: string | null;
  likeCount: number;
}

export interface FeedbackResponse {
  feedbacks: UserFeedback[];
  hasNext: boolean;
  nextCursorId: number;
}
