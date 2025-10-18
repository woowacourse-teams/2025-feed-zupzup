import { postAdminLogout } from '@/apis/admin.api';
import useNavigation from '@/domains/hooks/useNavigation';
import { NotificationService } from '@/services/notificationService';
import { resetLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';

export function useLogout() {
  const { goPath } = useNavigation();

  const { mutate: handleLogout } = useMutation({
    mutationFn: postAdminLogout,
    onSuccess: () => {
      resetLocalStorage('auth');
      goPath('/');
    },
    onSettled: () => {
      NotificationService.removeToken();
    },
  });

  return {
    handleLogout,
  };
}
