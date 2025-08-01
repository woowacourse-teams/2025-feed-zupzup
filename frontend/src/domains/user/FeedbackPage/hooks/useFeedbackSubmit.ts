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

  const submitFeedback = useCallback(
    async ({
      content,
      userName,
      isSecret,
      organizationId = 1,
    }: FeedbackSubmitParams) => {
      if (isSubmitting) return;

      setIsSubmitting(true);

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
          onError: () => {},
        });
      } catch (error) {
        console.error('피드백 제출 에러:', error);
      } finally {
        setIsSubmitting(false);
      }
    },
    [navigate, isSubmitting]
  );

  const handleFormSubmit = useCallback(
    (params: FeedbackSubmitParams, canSubmit: boolean) =>
      async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (canSubmit && !isSubmitting) {
          await submitFeedback(params);
        }
      },
    [submitFeedback, isSubmitting]
  );

  return {
    submitFeedback,
    handleFormSubmit,
    isSubmitting,
  };
}
