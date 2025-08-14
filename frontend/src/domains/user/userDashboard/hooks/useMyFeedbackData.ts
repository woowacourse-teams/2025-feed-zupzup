import { useState, useEffect } from 'react';
import { FeedbackType } from '@/types/feedback.types';
import { getMyFeedbacks } from '@/apis/userFeedback.api';
import { getLocalStorage } from '@/utils/localStorage';

export function useMyFeedbackData() {
  const [myFeedbacks, setMyFeedbacks] = useState<FeedbackType[]>([]);

  useEffect(() => {
    const fetchMyFeedbacks = async () => {
      const myFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
      if (myFeedbackIds.length === 0) return;

      const response = await getMyFeedbacks({
        organizationId: 1,
        feedbackIds: myFeedbackIds,
      });

      if (response?.data?.feedbacks) {
        setMyFeedbacks(response.data.feedbacks);
      }
    };

    fetchMyFeedbacks();
  }, []);

  return { myFeedbacks };
}
