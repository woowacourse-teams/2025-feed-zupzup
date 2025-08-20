import { CategoryType } from '@/analytics/types';
import { apiClient } from '@/apis/apiClient';
import {
  FeedbackType,
  SortType,
  SuggestionFeedback,
} from '@/types/feedback.types';

interface UserFeedbackParams {
  organizationId: string;
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

interface GetMyFeedbacksParams {
  organizationId: string;
  feedbackIds?: number[];
  orderBy?: SortType;
}

export async function getMyFeedbacks({
  organizationId,
  feedbackIds,
  orderBy,
}: GetMyFeedbacksParams) {
  const queryParams = new URLSearchParams();

  if (feedbackIds) {
    queryParams.append('feedbackIds', feedbackIds.join(','));
  }

  if (orderBy) {
    queryParams.append('orderBy', orderBy);
  }

  const queryString = queryParams.toString();
  const url = `/organizations/${organizationId}/feedbacks/my${queryString ? `?${queryString}` : ''}`;

  const response = await apiClient.get<{
    data: { feedbacks: FeedbackType[] };
  }>(url);
  return response;
}
