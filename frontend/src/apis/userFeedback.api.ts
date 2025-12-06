import { apiClient } from '@/apis/apiClient';
import { CategoryListType } from '@/constants/categoryList';
import { ApiResponse } from '@/types/apiResponse';
import { FeedbackType, SuggestionFeedbackData } from '@/types/feedback.types';

export interface UserFeedbackParams {
  organizationId: string;
  userName: string;
  isSecret: boolean;
  content: string;
  imageUrl: string | null;
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
  imageUrl: string | null;
}

export async function postUserFeedback({
  organizationId,
  isSecret,
  userName,
  content,
  imageUrl,
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
    imageUrl: imageUrl ?? '',
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

export interface GetMyLikedFeedbacksResponse {
  data: {
    feedbackIds: number[];
  };
}

interface GetMyLikedFeedbacksParams {
  organizationId: string;
}

export async function getMyLikedFeedbacks({
  organizationId,
}: GetMyLikedFeedbacksParams) {
  const response = await apiClient.get<GetMyLikedFeedbacksResponse>(
    `/organizations/${organizationId}/feedbacks/my-likes`
  );

  return response as GetMyLikedFeedbacksResponse;
}
