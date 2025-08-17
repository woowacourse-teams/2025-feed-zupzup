import { AdminAuthResponse, postAdminLogin } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { setLocalStorage } from '@/utils/localStorage';
import { useNavigate } from 'react-router-dom';

interface UseLoginProps {
  loginValue: {
    id: string;
    password: string;
  };
}

export default function useLogin({ loginValue }: UseLoginProps) {
  const navigate = useNavigate();
  const { showErrorModal } = useErrorModalContext();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    setLocalStorage('auth', null);

    try {
      await postAdminLogin({
        loginId: loginValue.id,
        password: loginValue.password,
        onSuccess: (response: AdminAuthResponse) => {
          setLocalStorage('auth', response.data);
          navigate(ADMIN_BASE + ROUTES.ADMIN_HOME);
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
