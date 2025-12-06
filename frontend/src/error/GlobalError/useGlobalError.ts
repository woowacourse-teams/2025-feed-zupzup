import { ApiError, NetworkError } from '@/apis/apiClient';
import { useQueryClient } from '@tanstack/react-query';
import { resetLocalStorage } from '@/utils/localStorage';
import { NotificationService } from '@/services/notificationService';
import useNavigation from '@/domains/hooks/useNavigation';
import { getErrorName } from '../error.utils';
import { ERROR_MESSAGES } from '../errorMessages';

interface useGlobalErrorProps {
  resetErrorBoundary: () => void;
  error: Error | ApiError | NetworkError;
}

export default function useGlobalError({
  resetErrorBoundary,
  error,
}: useGlobalErrorProps) {
  const queryClient = useQueryClient();
  const { goPath } = useNavigation();

  const handleRetry = () => {
    queryClient.resetQueries();
    resetErrorBoundary();
  };

  const handleLogin = () => {
    queryClient.resetQueries();
    resetLocalStorage('auth');
    NotificationService.removeToken();
    goPath('/login');
    resetErrorBoundary();
  };

  const errorName = getErrorName(error);
  const errorObject = ERROR_MESSAGES[errorName];
  errorObject.onClick = errorName === 'AUTH_ERROR' ? handleLogin : handleRetry;

  return { errorObject };
}
