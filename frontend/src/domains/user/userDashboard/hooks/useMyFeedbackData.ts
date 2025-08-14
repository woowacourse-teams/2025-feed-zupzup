import { useState, useEffect } from 'react';
import { FeedbackType } from '@/types/feedback.types';
import { getMyFeedbacks } from '@/apis/userFeedback.api';
import { getLocalStorage } from '@/utils/localStorage';

export function useMyFeedbackData() {
  const [myFeedbacks, setMyFeedbacks] = useState<FeedbackType[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchMyFeedbacks = async () => {
      const myFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
      if (myFeedbackIds.length === 0) return;

      setIsLoading(true);
      setError(null);

      try {
        const response = await getMyFeedbacks({
          organizationId: 1,
          feedbackIds: myFeedbackIds,
        });

        if (response?.data?.feedbacks) {
          setMyFeedbacks(response.data.feedbacks);
        }
      } catch (err) {
        console.error('내 피드백 초기 로드 중 오류:', err);
        setError('피드백을 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchMyFeedbacks();
  }, []);

  return { myFeedbacks, isLoading, error };
}
