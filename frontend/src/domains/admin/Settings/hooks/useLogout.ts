import { postAdminLogout } from '@/apis/admin.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { resetLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';

export function useLogout() {
  const { showErrorModal } = useErrorModalContext();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const response = await postAdminLogout();
      if (response.status !== 200) {
        throw new Error('로그아웃 실패');
      }
      resetLocalStorage('auth');
      navigate('/login');
    } catch (e) {
      showErrorModal(e, '에러');
    }
  };

  return {
    handleLogout,
  };
}
