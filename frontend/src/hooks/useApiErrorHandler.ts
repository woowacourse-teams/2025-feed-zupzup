import { ApiError } from '@/apis/apiClient';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useNavigation from '@/domains/hooks/useNavigation';
import { resetLocalStorage } from '@/utils/localStorage';

export function useApiErrorHandler() {
  const { goPath } = useNavigation();
  const { showErrorModal } = useErrorModalContext();

  const handleApiError = async (error: ApiError) => {
    if (error.status === 401) {
      resetLocalStorage('auth');
      showErrorModal(error, '로그인 권한 없음');
      goPath('/login');
    }
  };

  return { handleApiError };
}
