import { ApiError } from '@/apis/apiClient';
import useNavigation from '@/domains/hooks/useNavigation';
import { resetLocalStorage } from '@/utils/localStorage';
import { NotificationService } from '@/services/notificationService';
import { useErrorModalActions } from './useErrorModal';

export function useApiErrorHandler() {
  const { goPath } = useNavigation();
  const { showErrorModal } = useErrorModalActions();

  const handleApiError = async (error: ApiError) => {
    resetLocalStorage('auth');
    NotificationService.removeToken();
    goPath('/login');

    if (error.status === 401) {
      showErrorModal(error, '로그인 권한 없음');
    } else if (error.status === 403) {
      showErrorModal(error, '권한없음');
    } else {
      showErrorModal(error, '에러');
    }
  };

  return { handleApiError };
}
