import { postUserFeedback, UserFeedbackParams } from '@/apis/userFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { ApiResponse } from '@/types/apiResponse';
import { SuggestionFeedbackData } from '@/types/feedback.types';
import { StatusType } from '@/types/status.types';
import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useCallback, useState } from 'react';

export default function useFeedbackSubmit() {
  const [submitStatus, setSubmitStatus] = useState<StatusType>('idle');

  const queryClient = useQueryClient();

  const { mutate: submitFeedback } = useMutation<
    ApiResponse<SuggestionFeedbackData> | void,
    Error,
    UserFeedbackParams
  >({
    mutationFn: postUserFeedback,
    onMutate: () => {
      setSubmitStatus('submitting');
    },
    onSuccess: (response) => {
      if (!response) return;
      setLocalStorage('highlightedId', response.data.feedbackId);
      setLocalStorage('myFeedbacks', [
        ...(getLocalStorage<number[]>('myFeedbacks') || []),
        response.data.feedbackId,
      ]);
      setSubmitStatus('success');
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.infiniteFeedbacks });
    },
    onError: () => {
      setSubmitStatus('error');
    },
  });

  const resetStatus = useCallback(() => {
    setSubmitStatus('idle');
  }, []);

  return {
    submitFeedback,
    submitStatus,
    resetStatus,
  };
}
