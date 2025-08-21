import { postAdminLogout } from '@/apis/admin.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useNavigation from '@/domains/hooks/useNavigation';
import { resetLocalStorage } from '@/utils/localStorage';

export function useLogout() {
  const { showErrorModal } = useErrorModalContext();
  const { goPath } = useNavigation();

  const handleLogout = async () => {
    try {
      const response = await postAdminLogout();
      if (response.status !== 200) {
        throw new Error('로그아웃 실패');
      }
      resetLocalStorage('auth');
      goPath('/login');
    } catch (e) {
      showErrorModal(e, '에러');
    }
  };

  return {
    handleLogout,
  };
}
