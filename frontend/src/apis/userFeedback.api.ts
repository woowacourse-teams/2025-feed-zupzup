import { apiClient } from '@/apis/apiClient';
import { SuggestionFeedback } from '@/types/feedback.types';

interface UserFeedbackParams {
  organizationId: number;
  userName: string;
  isSecret: boolean;
  content: string;
  onSuccess: (data: SuggestionFeedback) => void;
  onError: () => void;
}

interface LikeParams {
  feedbackId: number;
  onSuccess: (data: SuggestionFeedback) => void;
  onError: () => void;
}

interface FeedbackRequestBody {
  content: string;
  isSecret: boolean;
  userName: string;
}

export async function postUserFeedback({
  organizationId,
  isSecret,
  userName,
  content,
  onSuccess,
  onError,
}: UserFeedbackParams) {
  try {
    const response = await apiClient.post<
      SuggestionFeedback,
      FeedbackRequestBody
    >(`/organizations/${organizationId}/feedbacks`, {
      content,
      isSecret,
      userName,
    });

    if (response) {
      onSuccess(response);
    }
  } catch (error) {
    console.error('피드백 전송 에러:', error);
  }
}

export async function postLike({ feedbackId, onSuccess, onError }: LikeParams) {
  try {
    const response = await apiClient.post(
      `/feedbacks/${feedbackId}/like`,
      {},
      {
        onSuccess,
        onError,
      }
    );
    return response;
  } catch (error) {
    console.error('피드백 전송 에러:', error);
    return;
  }
}

export async function deleteLike({
  feedbackId,
  onSuccess,
  onError,
}: LikeParams) {
  try {
    const response = await apiClient.delete(`/feedbacks/${feedbackId}/like`, {
      onSuccess,
      onError,
    });
    return response;
  } catch (error) {
    console.error('피드백 전송 에러:', error);
    return;
  }
}
