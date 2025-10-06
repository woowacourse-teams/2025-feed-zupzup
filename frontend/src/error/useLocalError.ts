import { ApiError } from '@/apis/apiClient';
import { useQueryClient } from '@tanstack/react-query';

interface useLocalErrorProps {
  queryKey: readonly string[];
  resetErrorBoundary: () => void;
  error: ApiError;
}

export default function useLocalError({
  queryKey,
  resetErrorBoundary,
  error,
}: useLocalErrorProps) {
  const queryClient = useQueryClient();

  const handleRetry = () => {
    queryClient.resetQueries({ queryKey: queryKey });
    resetErrorBoundary();
  };

  const ERROR_MESSAGES = {
    SERVER_ERROR: {
      title: '서버 오류',
      subtitle: '서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.',
      button: '재시도',
      onClick: handleRetry,
    },
    FAULT_REQUEST: {
      title: '잘못된 요청',
      subtitle: '잘못된 요청입니다. 다시 시도해주세요.',
      button: '재시도',
      onClick: handleRetry,
    },
    NETWORK_ERROR: {
      title: '네트워크 오류',
      subtitle: '네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      button: '재시도',
      onClick: handleRetry,
    },
  } as const;

  const errorName = getErrorName(error);
  const errorObject = ERROR_MESSAGES[errorName];

  return { errorObject };
}

function getErrorName(error: ApiError) {
  if (error.status === 500) {
    return 'SERVER_ERROR';
  }
  if (error.status === 400) {
    return 'FAULT_REQUEST';
  }
  if (error.status === 1000) {
    return 'NETWORK_ERROR';
  }
  return 'FAULT_REQUEST';
}
