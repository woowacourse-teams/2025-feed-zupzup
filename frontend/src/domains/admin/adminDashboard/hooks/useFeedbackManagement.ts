import { FeedbackType } from '@/types/feedback.types';
import { useState, useEffect } from 'react';

interface UseFeedbackManagementProps {
  originalFeedbacks: FeedbackType[];
}

export default function useFeedbackManagement({
  originalFeedbacks,
}: UseFeedbackManagementProps) {
  const [feedbacks, setFeedbacks] = useState<FeedbackType[]>([]);

  useEffect(() => {
    setFeedbacks(originalFeedbacks);
  }, [originalFeedbacks]);

  const optimisticConfirmFeedback = (feedbackId: number, comment: string) => {
    setFeedbacks((prevFeedbacks) =>
      prevFeedbacks.map((feedback) => {
        if (feedback.feedbackId === feedbackId) {
          return {
            ...feedback,
            comment,
            status: 'CONFIRMED' as FeedbackType['status'],
          };
        }
        return feedback;
      })
    );
  };

  const optimisticDeleteFeedback = (feedbackId: number) => {
    setFeedbacks((prevFeedbacks) =>
      prevFeedbacks.filter((feedback) => feedback.feedbackId !== feedbackId)
    );
  };

  return {
    feedbacks,
    optimisticConfirmFeedback,
    optimisticDeleteFeedback,
  };
}
