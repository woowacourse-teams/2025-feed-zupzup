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

type FeedbackStatus = 'idle' | 'loading' | 'success' | 'error';

export default function useFeedbackSubmit() {
  const navigate = useNavigate();
  const [modalStatus, setModalStatus] = useState<FeedbackStatus>('idle');
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
      setModalStatus('loading');

      try {
        await postUserFeedback({
          organizationId,
          content,
          isSecret,
          userName,
          onSuccess: (response: SuggestionFeedback) => {
            setLocalStorage('highlightedId', response.data.feedbackId);
            setModalStatus('success');
          },
          onError: () => {
            setModalStatus('error');
          },
        });
      } catch (e) {
        console.error(e);
        setModalStatus('error');
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

  const resetStatus = useCallback(() => {
    setModalStatus('idle');
  }, []);

  return {
    submitFeedback,
    handleFormSubmit,
    isSubmitting,
    modalStatus,
    resetStatus,
  };
}
