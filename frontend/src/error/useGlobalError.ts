import { ApiError } from '@/apis/apiClient';
import { useQueryClient } from '@tanstack/react-query';
import { resetLocalStorage } from '@/utils/localStorage';
import { NotificationService } from '@/services/notificationService';
import useNavigation from '@/domains/hooks/useNavigation';

interface useGlobalErrorProps {
  resetErrorBoundary: () => void;
  error: Error | ApiError;
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
    resetLocalStorage('auth');
    NotificationService.removeToken();
    goPath('/login');
    resetErrorBoundary();
  };

  const ERROR_MESSAGES = {
    UNKNOWN_ERROR: {
      title: '알 수 없는 오류',
      subtitle: '알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      button: '재시도',
      onClick: handleRetry,
    },
    AUTH_ERROR: {
      title: '인증 오류',
      subtitle: '인증이 필요합니다. 로그인 후 다시 시도해주세요.',
      button: '로그인하기',
      onClick: handleLogin,
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

function getErrorName(error: Error | ApiError) {
  if (error instanceof ApiError) {
    if (error.status === 401 || error.status === 403) {
      return 'AUTH_ERROR';
    }
    if (error.status === 1000) {
      return 'NETWORK_ERROR';
    }
  }

  return 'UNKNOWN_ERROR';
}
