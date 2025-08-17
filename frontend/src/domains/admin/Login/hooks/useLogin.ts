import { postAdminLogin } from '@/apis/admin.api';
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
    try {
      await postAdminLogin({
        loginId: loginValue.id,
        password: loginValue.password,
        onError: () => {
          showErrorModal(
            new ApiError(
              401,
              '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요'
            ),
            '로그인 요청 실패'
          );
        },
        onSuccess: (data: Response) => {
          navigate(ADMIN_BASE + ROUTES.ADMIN_HOME);
          setLocalStorage('auth', data);
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
