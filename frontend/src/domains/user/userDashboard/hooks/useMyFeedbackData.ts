import { useState, useEffect, useMemo } from 'react';
import { FeedbackType, SortType } from '@/types/feedback.types';
import { getMyFeedbacks } from '@/apis/userFeedback.api';
import { getLocalStorage } from '@/utils/localStorage';

export function useMyFeedbackData(selectedSort: SortType) {
  const [myFeedbacks, setMyFeedbacks] = useState<FeedbackType[]>([]);

  const feedbackIds = useMemo(
    () => [...new Set(getLocalStorage<number[]>('myFeedbacks') || [])],
    []
  );

  useEffect(() => {
    const fetchMyFeedbacks = async () => {
      if (feedbackIds.length === 0) return;

      const response = await getMyFeedbacks({
        organizationId: 1,
        feedbackIds,
        orderBy: selectedSort,
      });

      if (response?.data?.feedbacks) {
        setMyFeedbacks(response.data.feedbacks);
      }
    };

    fetchMyFeedbacks();
  }, [selectedSort, feedbackIds]);

  return { myFeedbacks };
}
