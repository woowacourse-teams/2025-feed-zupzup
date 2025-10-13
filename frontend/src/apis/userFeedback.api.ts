import { apiClient } from '@/apis/apiClient';
import { CategoryListType } from '@/constants/categoryList';
import { ApiResponse } from '@/types/apiResponse';
import {
  FeedbackType,
  SortType,
  SuggestionFeedbackData,
} from '@/types/feedback.types';

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
  feedbackIds?: number[];
  orderBy?: SortType;
}

export interface GetMyFeedbacksResponse {
  data: {
    feedbacks: FeedbackType[];
  };
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
