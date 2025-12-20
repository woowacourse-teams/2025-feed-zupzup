import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { useLogout } from '../../hooks/useLogout';

interface LogoutModalProps {
  onClose: () => void;
}

export default function LogoutModal({ onClose }: LogoutModalProps) {
  const { handleLogout } = useLogout();

  return (
    <ConfirmModal
      title='로그아웃'
      message='정말 로그아웃하시겠습니까?'
      onConfirm={handleLogout}
      onClose={onClose}
    />
  );
}
