import { postAdminSignup } from '@/apis/admin.api';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { setLocalStorage } from '@/utils/localStorage';
import { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';

interface UseSignupProps {
  confirmPasswordErrors: string;
  errors: {
    [key: string]: string;
  };
  signUpValue: {
    name: string;
    id: string;
    password: string;
  };
  setToast: (message: string | null) => void;
}

export default function useSignup({
  confirmPasswordErrors,
  errors,
  signUpValue,
  setToast,
}: UseSignupProps) {
  const navigate = useNavigate();
  const { showErrorModal } = useErrorModalContext();

  const handleSignUp = async (event: FormEvent) => {
    event.preventDefault();
    const isValid = Object.values(errors).every((error) => !error);
    const isConfirmPasswordValid =
      !confirmPasswordErrors && signUpValue.password !== '';

    if (!isValid || !isConfirmPasswordValid) {
      setToast('입력하신 정보를 다시 확인해주세요.');
      return;
    }

    try {
      await postAdminSignup({
        loginId: signUpValue.id,
        password: signUpValue.password,
        adminName: signUpValue.name,
        onError: () => {
          showErrorModal(
            new Error('회원가입에 실패했습니다. 다시 시도해주세요.')
          );
        },
        onSuccess: (authData: Response) => {
          setLocalStorage('auth', authData);
          navigate(ADMIN_BASE + ROUTES.ADMIN_HOME);
        },
      });
    } catch (error) {
      showErrorModal(error, '회원가입 요청에 실패했습니다. 다시 시도해주세요.');
    }
  };

  return {
    handleSignUp,
  };
}
