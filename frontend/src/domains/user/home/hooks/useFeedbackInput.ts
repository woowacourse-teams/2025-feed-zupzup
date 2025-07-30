import { useState, useCallback } from 'react';
import { FEEDBACK_INPUT_CONSTANTS } from '@/domains/user/home/constants/feedbackInput';

export interface UseFeedbackInputReturn {
  feedback: string;
  handleFeedbackChange: (value: string) => void;
  resetFeedback: () => void;
}

export function useFeedbackInput(): UseFeedbackInputReturn {
  const [feedback, setFeedback] = useState<string>(
    FEEDBACK_INPUT_CONSTANTS.DEFAULTS.FEEDBACK
  );

  const handleFeedbackChange = useCallback((value: string) => {
    setFeedback(value as typeof feedback);
  }, []);

  const resetFeedback = useCallback(() => {
    setFeedback(FEEDBACK_INPUT_CONSTANTS.DEFAULTS.FEEDBACK);
  }, []);

  return {
    feedback,
    handleFeedbackChange,
    resetFeedback,
  };
}
