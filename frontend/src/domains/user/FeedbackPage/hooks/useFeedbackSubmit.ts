import { postUserFeedback } from '@/apis/userFeedback.api';
import { SuggestionFeedback } from '@/types/feedback.types';
import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';
import { useCallback, useState } from 'react';
import { StatusType } from '@/types/status.types';
import { useNavigate } from 'react-router-dom';
import { CategoryType } from '@/analytics/types';

interface FeedbackSubmitParams {
  content: string;
  userName: string;
  isSecret: boolean;
  category: CategoryType | null;
  organizationId?: string;
}

export default function useFeedbackSubmit() {
  const navigate = useNavigate();
  const [submitStatus, setSubmitStatus] = useState<StatusType>('idle');

  const submitFeedback = useCallback(
    async ({
      content,
      userName,
      isSecret,
      category,
      organizationId = '1',
    }: FeedbackSubmitParams) => {
      if (submitStatus === 'submitting') return;

      setSubmitStatus('submitting');

      try {
        await postUserFeedback({
          organizationId,
          content,
          isSecret,
          userName,
          category,
          onSuccess: (response: SuggestionFeedback) => {
            setLocalStorage('highlightedId', response.data.feedbackId);
            setLocalStorage('myFeedbacks', [
              ...(getLocalStorage<number[]>('myFeedbacks') || []),
              response.data.feedbackId,
            ]);
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
