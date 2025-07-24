import { apiClient } from '@/apis/apiClient';

interface DeleteFeedbackParams {
  feedbackId: number;
}

interface PatchFeedbackStatusParams {
  feedbackId: number;
  status: string;
}

export async function deleteFeedback({ feedbackId }: DeleteFeedbackParams) {
  try {
    const response = await apiClient.delete(
      `/api/admin/feedbacks/${feedbackId}`
    );
    if (!response) return;
  } catch (error) {
    console.error('피드백 제거 에러:', error);
  }
}

export async function patchFeedbackStatus({
  feedbackId,
  status,
}: PatchFeedbackStatusParams) {
  try {
    const response = await apiClient.patch(
      `/api/admin/feedbacks/${feedbackId}/status`,
      { status }
    );
    if (!response) return;
  } catch (error) {
    console.error('피드백 상태 변경 에러:', error);
  }
}
