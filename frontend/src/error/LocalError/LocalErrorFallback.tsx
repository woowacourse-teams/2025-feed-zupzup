import useLocalError from './useLocalError';
import CommonErrorFallback from '../CommonErrorFallback/CommonErrorFallback';
import { ApiError, NetworkError } from '@/apis/apiClient';

type LocalErrorType = ApiError | NetworkError;
interface LocalErrorFallbackProps {
  resetErrorBoundary: () => void;
  error: LocalErrorType;
  queryKey: readonly string[];
}

export default function LocalErrorFallback({
  resetErrorBoundary,
  error,
  queryKey,
}: LocalErrorFallbackProps) {
  const { errorObject } = useLocalError({
    queryKey,
    resetErrorBoundary,
    error,
  });

  return <CommonErrorFallback errorObject={errorObject} />;
}
