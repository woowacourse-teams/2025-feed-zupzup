import { CategoryType } from '@/analytics/types';
import { apiClient } from '@/apis/apiClient';
import { SortType, SuggestionFeedback } from '@/types/feedback.types';
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
    data: { feedbacks: MyFeedbackResponse[] };
  }>(url);
  return response;
}
