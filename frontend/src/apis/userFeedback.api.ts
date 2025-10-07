import { apiClient } from '@/apis/apiClient';
import { CategoryListType } from '@/constants/categoryList';
import { ApiResponse } from '@/types/apiResponse';
import { FeedbackType, SuggestionFeedbackData } from '@/types/feedback.types';

export interface UserFeedbackParams {
  organizationId: string;
  userName: string;
  isSecret: boolean;
  content: string;
  category: CategoryListType | null;
}

interface LikeParams {
  feedbackId: number;
}

interface FeedbackRequestBody {
  content: string;
  isSecret: boolean;
  userName: string;
  category: CategoryListType | null;
}

export async function postUserFeedback({
  organizationId,
  isSecret,
  userName,
  content,
  category,
}: UserFeedbackParams) {
  const response = await apiClient.post<
    ApiResponse<SuggestionFeedbackData>,
    FeedbackRequestBody
  >(`/organizations/${organizationId}/feedbacks`, {
    content,
    isSecret,
    userName,
    category,
  });
  return response;
}

export async function postLike({ feedbackId }: LikeParams) {
  await apiClient.patch(`/feedbacks/${feedbackId}/like`, {});
}

export async function deleteLike({ feedbackId }: LikeParams) {
  const response = await apiClient.patch(`/feedbacks/${feedbackId}/unlike`, {});
  return response;
}

export interface GetMyFeedbacksParams {
  organizationId: string;
}

export interface GetMyFeedbacksResponse {
  data: {
    feedbacks: FeedbackType[];
  };
}

export async function getMyFeedbacks({ organizationId }: GetMyFeedbacksParams) {
  const url = `/organizations/${organizationId}/feedbacks/my`;

  const response = await apiClient.get<GetMyFeedbacksResponse>(url);

  return response as GetMyFeedbacksResponse;
}
