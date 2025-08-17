import { useState, useEffect, useMemo } from 'react';
import { FeedbackType, SortType } from '@/types/feedback.types';
import { getMyFeedbacks } from '@/apis/userFeedback.api';
import { getLocalStorage } from '@/utils/localStorage';
import { useParams } from 'react-router-dom';

export function useMyFeedbackData(selectedSort: SortType) {
  const { id: organizationId } = useParams();
  const [myFeedbacks, setMyFeedbacks] = useState<FeedbackType[]>([]);

  const feedbackIds = useMemo(
    () => [...new Set(getLocalStorage<number[]>('myFeedbacks') || [])],
    []
  );

  useEffect(() => {
    const fetchMyFeedbacks = async () => {
      if (feedbackIds.length === 0) return;

      const response = await getMyFeedbacks({
        organizationId: organizationId || '',
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
