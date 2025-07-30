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

  const confirmFeedback = (feedbackId: number) => {
    setFeedbacks((prevFeedbacks) =>
      prevFeedbacks.map((feedback) => {
        if (feedback.feedbackId === feedbackId) {
          return {
            ...feedback,
            status: 'CONFIRMED' as FeedbackType['status'],
          };
        }
        return feedback;
      })
    );
  };

  const deleteFeedback = (feedbackId: number) => {
    setFeedbacks((prevFeedbacks) =>
      prevFeedbacks.filter((feedback) => feedback.feedbackId !== feedbackId)
    );
  };

  return {
    feedbacks,
    confirmFeedback,
    deleteFeedback,
  };
}
