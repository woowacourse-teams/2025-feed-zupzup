import { useState, useEffect } from 'react';
import { AdminFeedback } from '@/types/feedback.types';

interface UseFeedbackManagementProps {
  originalFeedbacks: AdminFeedback[];
}

export default function useFeedbackManagement({
  originalFeedbacks,
}: UseFeedbackManagementProps) {
  const [feedbacks, setFeedbacks] = useState<AdminFeedback[]>([]);

  useEffect(() => {
    setFeedbacks(originalFeedbacks);
  }, [originalFeedbacks]);

  const confirmFeedback = (feedbackId: number) => {
    setFeedbacks((prevFeedbacks) =>
      prevFeedbacks.map((feedback) => {
        if (feedback.feedbackId === feedbackId) {
          return {
            ...feedback,
            status: 'CONFIRMED' as AdminFeedback['status'],
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
