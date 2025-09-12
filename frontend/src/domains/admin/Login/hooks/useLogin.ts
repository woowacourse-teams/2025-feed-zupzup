import { AdminAuthResponse, postAdminLogin } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useNavigation from '@/domains/hooks/useNavigation';
import { setLocalStorage } from '@/utils/localStorage';
import { NotificationService } from '@/services/notificationService';

interface UseLoginProps {
  loginValue: {
    id: string;
    password: string;
  };
}

export default function useLogin({ loginValue }: UseLoginProps) {
  const { goPath } = useNavigation();
  const { showErrorModal } = useErrorModalContext();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    setLocalStorage('auth', null);
    NotificationService.removeToken();

    try {
      await postAdminLogin({
        loginId: loginValue.id,
        password: loginValue.password,
        onSuccess: (response: AdminAuthResponse) => {
          setLocalStorage('auth', response.data);
          goPath(ADMIN_BASE + ROUTES.ADMIN_HOME);
        },
      });
    } catch (error: ApiError | unknown) {
      showErrorModal(error as ApiError, '로그인 요청 실패');
    }
  };

  return {
    handleSubmit,
  };
}
