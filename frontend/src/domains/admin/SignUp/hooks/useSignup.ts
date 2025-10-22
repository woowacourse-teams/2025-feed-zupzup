import {
  AdminAuthResponse,
  postAdminSignup,
  PostAdminSignupParams,
} from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import useNavigation from '@/domains/hooks/useNavigation';
import { NotificationService } from '@/services/notificationService';
import { setLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';
import { FormEvent } from 'react';

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

  const { mutate: adminSignup, isPending } = useMutation<
    AdminAuthResponse,
    ApiError,
    PostAdminSignupParams
  >({
    mutationFn: postAdminSignup,
    onSuccess: (response) => {
      setLocalStorage('auth', response.data);
      goPath(ADMIN_BASE + ROUTES.ADMIN_HOME);
      NotificationService.removeToken();
    },
  });

  const handleSignUp = async (event: FormEvent) => {
    event.preventDefault();
    const isValid = Object.values(errors).every((error) => !error);
    const isConfirmPasswordValid =
      !confirmPasswordErrors && signUpValue.password !== '';

    if (
      signUpValue.name === '' ||
      signUpValue.id === '' ||
      signUpValue.password === ''
    ) {
      setToast('모든 항목을 입력해주세요.');
      return;
    }

    if (!isConfirmPasswordValid) {
      setToast('비밀번호 확인을 다시 확인해주세요.');
      return;
    }

    if (!isValid) {
      setToast('입력하신 정보를 다시 확인해주세요.');
      return;
    }

    adminSignup({
      loginId: signUpValue.id,
      password: signUpValue.password,
      adminName: signUpValue.name,
    });
  };

  return {
    handleSignUp,
    isLoading: isPending,
  };
}
