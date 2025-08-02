import { postUserFeedback } from '@/apis/userFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
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
  const { showErrorModal } = useErrorModalContext();

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
          },
          onError: () => {},
        });
      } catch (e) {
        showErrorModal(e, '에러');
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
