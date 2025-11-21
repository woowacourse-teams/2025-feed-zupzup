import { deleteAdmin } from '@/apis/admin.api';
import useNavigation from '@/domains/hooks/useNavigation';
import { NotificationService } from '@/services/notificationService';
import { resetLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';
import { useToast } from '@/contexts/useToast';

export function useWithdraw() {
  const { goPath } = useNavigation();
  const { showToast } = useToast();

  const { mutateAsync: handleWithdraw } = useMutation({
    mutationFn: deleteAdmin,
    onSuccess: () => {
      resetLocalStorage('auth');
      NotificationService.removeToken();
      goPath('/');
    },
    onError: () => {
      showToast('탈퇴 처리 중 문제가 발생했어요. 잠시 후 다시 시도해주세요');
    },
  });

  return {
    handleWithdraw,
  };
}
