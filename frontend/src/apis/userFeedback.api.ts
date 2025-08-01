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
  await apiClient.post<SuggestionFeedback, FeedbackRequestBody>(
    `/organizations/${organizationId}/feedbacks`,
    {
      content,
      isSecret,
      userName,
    },
    { onError, onSuccess }
  );
}

export async function postLike({ feedbackId, onSuccess, onError }: LikeParams) {
  await apiClient.post(
    `/feedbacks/${feedbackId}/like`,
    {},
    {
      onSuccess,
      onError,
    }
  );
}

export async function deleteLike({
  feedbackId,
  onSuccess,
  onError,
}: LikeParams) {
  const response = await apiClient.delete(`/feedbacks/${feedbackId}/like`, {
    onSuccess,
    onError,
  });
  return response;
}
