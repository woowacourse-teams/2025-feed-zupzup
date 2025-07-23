import { apiClient } from '@/apis/apiClient';

interface UserFeedbackParams {
  placeId: number;
  content: string;
  userName: string;
  imageUrl: string;
  isSecret: boolean;
}

export async function postUserFeedback({
  placeId,
  content,
  imageUrl,
  isSecret,
  userName,
}: UserFeedbackParams) {
  try {
    const response = await apiClient.post(`/api/places/${placeId}/feedbacks`, {
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
