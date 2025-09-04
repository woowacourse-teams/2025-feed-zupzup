import { AdminAuthResponse, postAdminSignup } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useNavigation from '@/domains/hooks/useNavigation';
import { setLocalStorage } from '@/utils/localStorage';
import { FormEvent, useState } from 'react';
import { NotificationService } from '@/services/notificationService';
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
  const { goPath } = useNavigation();
  const { showErrorModal } = useErrorModalContext();
  const [isLoading, setIsLoading] = useState(false);

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
      setIsLoading(true);
      await postAdminSignup({
        loginId: signUpValue.id,
        password: signUpValue.password,
        adminName: signUpValue.name,
        onSuccess: (response: AdminAuthResponse) => {
          setLocalStorage('auth', response.data);
          goPath(ADMIN_BASE + ROUTES.ADMIN_HOME);
          NotificationService.removeToken();
        },
      });
    } catch (error) {
      showErrorModal(error as ApiError, '회원가입 요청 실패');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    handleSignUp,
    isLoading,
  };
}
