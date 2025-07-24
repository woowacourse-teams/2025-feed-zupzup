import { apiClient } from '@/apis/apiClient';

interface UserFeedbackParams {
  placeId: number;
  content: string;
  userName: string;
  imageUrl: string;
  isSecret: boolean;
}

interface PostLikeParams {
  feedbackId: number;
}

export async function postUserFeedback({
  placeId,
  content,
  imageUrl,
  isSecret,
  userName,
}: UserFeedbackParams) {
  try {
    const response = await apiClient.post(`/places/${placeId}/feedbacks`, {
      content,
      imageUrl,
      isSecret,
      userName,
    });
    if (!response) return;
  } catch (error) {
    console.error('피드백 전송 에러:', error);
  }
}

export async function postLike({ feedbackId }: PostLikeParams) {
  try {
    const response = await apiClient.post(`/feedbacks/${feedbackId}/like`, {});
    return response;
  } catch (error) {
    console.error('피드백 전송 에러:', error);
    return;
  }
}
