import {
  AdminAuthResponse,
  postAdminSignup,
  PostAdminSignupParams,
} from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import useNavigation from '@/domains/hooks/useNavigation';
import { useErrorModalActions } from '@/hooks/useErrorModal';
import { NotificationService } from '@/services/notificationService';
import { useToastActions } from '@/stores/Toast/useToast';
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
}

export default function useSignup({
  confirmPasswordErrors,
  errors,
  signUpValue,
}: UseSignupProps) {
  const { goPath } = useNavigation();
  const { showErrorModal } = useErrorModalActions();
  const { showToast } = useToastActions();

  const { mutate: adminSignup, isPending } = useMutation<
    AdminAuthResponse,
    ApiError,
    PostAdminSignupParams
  >({
    mutationFn: postAdminSignup,
    onError: (error) => {
      showErrorModal(error, '회원가입 요청 실패');
    },
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

    if (!isValid || !isConfirmPasswordValid) {
      showToast('입력하신 정보를 다시 확인해주세요.');
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
