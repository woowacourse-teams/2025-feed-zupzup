import { ApiError } from '@/apis/apiClient';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useNavigation from '@/domains/hooks/useNavigation';
import { resetLocalStorage } from '@/utils/localStorage';

export function useApiErrorHandler() {
  const { goPath } = useNavigation();
  const { showErrorModal } = useErrorModalContext();

  const handleApiError = async (error: ApiError) => {
    resetLocalStorage('auth');
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
