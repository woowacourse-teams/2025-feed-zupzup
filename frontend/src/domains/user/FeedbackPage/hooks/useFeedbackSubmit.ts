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

type FeedbackStatus = 'idle' | 'submitting' | 'success' | 'error';

export default function useFeedbackSubmit() {
  const navigate = useNavigate();
  const [submitStatus, setSubmitStatus] = useState<FeedbackStatus>('idle');

  const submitFeedback = useCallback(
    async ({
      content,
      userName,
      isSecret,
      organizationId = 1,
    }: FeedbackSubmitParams) => {
      if (submitStatus === 'submitting') return;

      setSubmitStatus('submitting');

      try {
        await postUserFeedback({
          organizationId,
          content,
          isSecret,
          userName,
          onSuccess: (response: SuggestionFeedback) => {
            setLocalStorage('highlightedId', response.data.feedbackId);
            setSubmitStatus('success');
          },
          onError: () => {
            setSubmitStatus('error');
          },
        });
      } catch (e) {
        console.error(e);
        setSubmitStatus('error');
      }
    },
    [navigate, submitStatus]
  );

  const resetStatus = useCallback(() => {
    setSubmitStatus('idle');
  }, []);

  return {
    submitFeedback,
    submitStatus,
    resetStatus,
  };
}
