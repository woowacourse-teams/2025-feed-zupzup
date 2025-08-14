import { useState, useEffect } from 'react';
import { FeedbackType, SortType } from '@/types/feedback.types';
import { getMyFeedbacks } from '@/apis/userFeedback.api';
import { getLocalStorage } from '@/utils/localStorage';

export function useMyFeedbackData(selectedSort: SortType) {
  const [myFeedbacks, setMyFeedbacks] = useState<FeedbackType[]>([]);

  useEffect(() => {
    const fetchMyFeedbacks = async () => {
      const myFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
      if (myFeedbackIds.length === 0) return;

      const response = await getMyFeedbacks({
        organizationId: 1,
        feedbackIds: myFeedbackIds,
        orderBy: selectedSort,
      });

      if (response?.data?.feedbacks) {
        setMyFeedbacks(response.data.feedbacks);
      }
    };

    fetchMyFeedbacks();
  }, [selectedSort]);

  return { myFeedbacks };
}
