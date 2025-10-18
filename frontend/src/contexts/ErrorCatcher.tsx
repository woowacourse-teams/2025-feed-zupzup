import { ApiError } from '@/apis/apiClient';
import { useErrorContext } from '@/contexts/useErrorContext';
import { useToast } from '@/contexts/useToast';
import { useEffect } from 'react';

export const ErrorCatcher = () => {
  const { appError } = useErrorContext();
  const { showToast } = useToast();

  useEffect(() => {
    if (!appError) return;
    console.log('에러 캐쳐 start', appError);

    if (appError instanceof ApiError) {
      console.log('에러 캐쳐 ApiError 처리', appError);
      showToast(appError.message || '알 수 없는 오류가 발생했습니다.');
      return;
    }

    console.log('에러 캐쳐 알 수 없는 에러 처리', appError);
    throw appError;
  }, [appError]);

  return <></>;
};
