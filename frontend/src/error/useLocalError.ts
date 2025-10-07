import { ApiError } from '@/apis/apiClient';
import { useQueryClient } from '@tanstack/react-query';
import { ERROR_MESSAGES } from './errorMessages';
import { getErrorName } from './error.utils';

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

  const errorName = getErrorName(error);
  const errorObject = ERROR_MESSAGES[errorName];
  errorObject.onClick = handleRetry;

  return { errorObject };
}
