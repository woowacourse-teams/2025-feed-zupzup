import { apiClient } from '@/apis/apiClient';
import { SuggestionFeedback } from '@/types/feedback.types';

interface UserFeedbackParams {
  placeId: number;
  content: string;
  userName: string;
  imageUrl: string;
  isSecret: boolean;
  onSuccess: (data: SuggestionFeedback) => void;
  onError: () => void;
}

interface LikeParams {
  feedbackId: number;
  onSuccess: (data: SuggestionFeedback) => void;
  onError: () => void;
}

export async function postUserFeedback({
  placeId,
  content,
  imageUrl,
  isSecret,
  userName,
  onSuccess,
  onError,
}: UserFeedbackParams) {
  try {
    const response = await apiClient.post(
      `/places/${placeId}/feedbacks`,
      {
        content,
        imageUrl,
        isSecret,
        userName,
      },
      {
        onSuccess,
        onError,
      }
    );
    if (!response) return;
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
