import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

interface DeleteFeedbackParams {
  feedbackId: number;
}

interface PatchFeedbackStatusParams {
  feedbackId: number;
  comment: string;
}

type FeedbackStatisticsType = {
  confirmedCount: number;
  totalCount: number;
  reflectionRate: number;
};

export type GetFeedbackStatisticsResponse = ApiResponse<FeedbackStatisticsType>;

export async function deleteFeedback({ feedbackId }: DeleteFeedbackParams) {
  const response = await apiClient.delete(`/admin/feedbacks/${feedbackId}`);
  if (!response) return;
}

export async function patchFeedbackStatus({
  feedbackId,
  comment,
}: PatchFeedbackStatusParams) {
  const response = await apiClient.patch(
    `/admin/feedbacks/${feedbackId}/comment`,
    { comment }
  );
  if (!response) return;
}

export async function getFeedbackStatistics(): Promise<GetFeedbackStatisticsResponse> {
  const response = await apiClient.get('/admin/feedbacks/statistics');

  return response as GetFeedbackStatisticsResponse;
}
