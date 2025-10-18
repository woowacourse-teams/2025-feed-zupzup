import { ApiError } from '@/apis/apiClient';
import { useErrorContext } from '@/contexts/useErrorContext';
import { useToast } from '@/contexts/useToast';
import { useEffect } from 'react';

export const ErrorCatcher = () => {
  const { appError } = useErrorContext();
  const { showToast } = useToast();

  useEffect(() => {
    if (!appError) return;

    if (appError instanceof ApiError) {
      showToast(appError.message || '알 수 없는 오류가 발생했습니다.');
      return;
    }

    throw appError;
  }, [appError]);

  return <></>;
};
