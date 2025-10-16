import { ApiError, NetworkError } from '@/apis/apiClient';
import useGlobalError from './useGlobalError';
import CommonErrorFallback from '../CommonErrorFallback/CommonErrorFallback';

type GlobalErrorType = Error | ApiError | NetworkError;

interface GlobalErrorFallbackProps {
  resetErrorBoundary: () => void;
  error: GlobalErrorType;
}

export default function GlobalErrorFallback({
  resetErrorBoundary,
  error,
}: GlobalErrorFallbackProps) {
  const { errorObject } = useGlobalError({
    resetErrorBoundary,
    error,
  });

  return <CommonErrorFallback errorObject={errorObject} />;
}
