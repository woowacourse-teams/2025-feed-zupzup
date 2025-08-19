import { postAdminLogout } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { resetLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';

export function useApiErrorHandler() {
  const navigate = useNavigate();
  const { showErrorModal } = useErrorModalContext();

  const handleApiError = async (error: ApiError) => {
    if (error.status === 401) {
      try {
        await postAdminLogout();
        resetLocalStorage('auth');
        showErrorModal(error, '로그인 권한 없음');
        navigate('/login');
      } catch (e) {
        console.log('로그아웃 실패', e);
      }
    }
  };

  return { handleApiError };
}
