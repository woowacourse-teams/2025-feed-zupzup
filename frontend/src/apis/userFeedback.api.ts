import { CategoryType } from '@/analytics/types';
import { apiClient } from '@/apis/apiClient';
import { SuggestionFeedback } from '@/types/feedback.types';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

interface UserFeedbackParams {
  organizationId: number;
  userName: string;
  isSecret: boolean;
  content: string;
  category: CategoryType | null;
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
  category: CategoryType | null;
}

export async function postUserFeedback({
  organizationId,
  isSecret,
  userName,
  content,
  category,
  onSuccess,
  onError,
}: UserFeedbackParams) {
  await apiClient.post<SuggestionFeedback, FeedbackRequestBody>(
    `/organizations/${organizationId}/feedbacks`,
    {
      content,
      isSecret,
      userName,
      category,
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

export interface MyFeedbackResponse {
  feedbackId: number;
  content: string;
  status: FeedbackStatusType;
  isSecret: boolean;
  likeCount: number;
  userName: string;
  postedAt: string;
  category: CategoryType;
  comment: string | null;
}

interface GetMyFeedbacksParams {
  organizationId: number;
  feedbackIds?: number[];
}

export async function getMyFeedbacks({
  organizationId,
  feedbackIds,
}: GetMyFeedbacksParams) {
  const queryParams = feedbackIds ? `?feedbackIds=${feedbackIds}` : '';
  const response = await apiClient.get<{
    data: { feedbacks: MyFeedbackResponse[] };
  }>(`/organizations/${organizationId}/feedbacks/my${queryParams}`);
  return response;
}
