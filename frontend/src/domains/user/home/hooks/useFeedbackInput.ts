import { useState, useCallback } from 'react';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';

export interface UseFeedbackInputReturn {
  feedback: string;
  handleFeedbackChange: (event: React.ChangeEvent<HTMLTextAreaElement>) => void;
  resetFeedback: () => void;
}

export function useFeedbackInput(): UseFeedbackInputReturn {
  const [feedback, setFeedback] = useState(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.FEEDBACK
  );

  const handleFeedbackChange = useCallback(
    (event: React.ChangeEvent<HTMLTextAreaElement>) => {
      setFeedback(event.target.value as typeof feedback);
    },
    []
  );

  const resetFeedback = useCallback(() => {
    setFeedback(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.FEEDBACK);
  }, []);

  return {
    feedback,
    handleFeedbackChange,
    resetFeedback,
  };
}
