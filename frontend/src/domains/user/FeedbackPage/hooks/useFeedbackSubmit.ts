import { postUserFeedback } from '@/apis/userFeedback.api';
import { SuggestionFeedback } from '@/types/feedback.types';
import { setLocalStorage } from '@/utils/localStorage';
import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface FeedbackSubmitParams {
  content: string;
  userName: string;
  isSecret: boolean;
  organizationId?: number;
}

export default function useFeedbackSubmit() {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submitFeedback = useCallback(
    async ({
      content,
      userName,
      isSecret,
      organizationId = 1,
    }: FeedbackSubmitParams) => {
      if (isSubmitting) return;

      setIsSubmitting(true);
      setError(null);

      try {
        await postUserFeedback({
          organizationId,
          content,
          isSecret,
          userName,
          onSuccess: (response: SuggestionFeedback) => {
            setLocalStorage('highlightedId', response.data.feedbackId);
            navigate('/dashboard');
          },
          onError: () => {
            setError('피드백 제출에 실패했습니다. 다시 시도해주세요.');
          },
        });
      } catch (error) {
        console.error('피드백 제출 에러:', error);
        setError('네트워크 오류가 발생했습니다. 다시 시도해주세요.');
      } finally {
        setIsSubmitting(false);
      }
    },
    [navigate, isSubmitting]
  );

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    submitFeedback,
    isSubmitting,
    error,
    clearError,
  };
}
