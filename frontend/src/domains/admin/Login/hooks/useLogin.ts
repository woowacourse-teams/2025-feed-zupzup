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

  const handleError = (error: ApiError) => {
    showErrorModal(error, '로그인 요청 실패');
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      await postAdminLogin({
        loginId: loginValue.id,
        password: loginValue.password,
        onError: () => {
          handleError(
            new ApiError(
              401,
              '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요'
            )
          );
        },
        onSuccess: (response: AdminAuthResponse) => {
          console.log('로그인 성공', response.data);
          navigate(ADMIN_BASE + ROUTES.ADMIN_HOME);
          setLocalStorage('auth', response.data);
        },
      });
    } catch (error: ApiError | unknown) {
      handleError(error as ApiError);
    }
  };

  return {
    handleSubmit,
  };
}
