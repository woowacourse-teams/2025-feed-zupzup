import { apiClient } from '@/apis/apiClient';

interface DeleteFeedbackParams {
  feedbackId: number;
}

interface PatchFeedbackStatusParams {
  feedbackId: number;
  comment: string;
}

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
