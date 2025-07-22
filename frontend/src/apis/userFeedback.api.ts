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
    const response = await apiClient.post(
      `${process.env.BASE_URL}/api/places/${placeId}/feedbacks`,
      {
        content,
        imageUrl,
        isSecret,
        userName,
      },
      {
        onSuccess: (res) => console.log('피드백 전송 성공:', res),
        onError: () => console.error('피드백 전송 실패:'),
      }
    );
    if (!response) return;
  } catch (error) {
    console.error('피드백 전송 에러:', error);
  }
}
