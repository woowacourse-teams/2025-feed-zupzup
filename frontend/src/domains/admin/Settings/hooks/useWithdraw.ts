import { deleteAdmin } from '@/apis/admin.api';
import useNavigation from '@/domains/hooks/useNavigation';
import { NotificationService } from '@/services/notificationService';
import { resetLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';

export function useWithdraw() {
  const { goPath } = useNavigation();

  const { mutateAsync: handleWithdraw } = useMutation({
    mutationFn: deleteAdmin,
    onSuccess: () => {
      resetLocalStorage('auth');
      NotificationService.removeToken();
      goPath('/');
    },
  });

  return {
    handleWithdraw,
  };
}
