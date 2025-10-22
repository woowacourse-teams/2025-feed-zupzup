import { postAdminLogout } from '@/apis/admin.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useNavigation from '@/domains/hooks/useNavigation';
import { resetLocalStorage } from '@/utils/localStorage';
import { NotificationService } from '@/services/notificationService';
import { useMutation } from '@tanstack/react-query';

export function useLogout() {
  const { showErrorModal } = useErrorModalContext();
  const { goPath } = useNavigation();

  const { mutate: handleLogout } = useMutation({
    mutationFn: postAdminLogout,
    onError: (e) => {
      showErrorModal(e, '에러');
    },
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
