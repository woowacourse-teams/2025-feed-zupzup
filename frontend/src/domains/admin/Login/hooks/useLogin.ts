import {
  AdminAuthResponse,
  postAdminLogin,
  PostAdminLoginParams,
} from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import useNavigation from '@/domains/hooks/useNavigation';
import { NotificationService } from '@/services/notificationService';
import { setLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';

interface UseLoginProps {
  loginValue: {
    id: string;
    password: string;
  };
}

export default function useLogin({ loginValue }: UseLoginProps) {
  const { goPath } = useNavigation();

  const { mutate: adminLogin } = useMutation<
    AdminAuthResponse | void,
    ApiError,
    PostAdminLoginParams
  >({
    mutationFn: postAdminLogin,
    onSuccess: async (response) => {
      setLocalStorage('auth', response?.data || null);

      if (
        NotificationService.checkIsSupported() &&
        NotificationService.getCurrentPermission() === 'default'
      ) {
        try {
          await NotificationService.enable();
        } catch (error) {
          console.log('알림 권한 요청 실패 (무시됨):', error);
        }
      }

      goPath(ADMIN_BASE + ROUTES.ADMIN_HOME);
    },
  });

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    setLocalStorage('auth', null);
    NotificationService.removeToken();

    adminLogin({
      loginId: loginValue.id,
      password: loginValue.password,
    });
  };

  return {
    handleSubmit,
  };
}
