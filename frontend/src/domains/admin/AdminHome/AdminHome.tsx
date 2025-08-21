import PlusIcon from '@/components/icons/PlusIcon';
import { useModalContext } from '@/contexts/useModal';
import {
  addAdminOrganization,
  adminOrganizationListContainer,
  homeLayout,
  infoContainer,
  listCaption,
  listTitle,
} from '@/domains/admin/AdminHome/AdminHome.style';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import AdminOrganizationList from '@/domains/admin/AdminHome/components/AdminOrganizationList/AdminOrganizationList';
import useAdminOrganization from '@/domains/admin/AdminHome/hooks/useAdminOrganization';
import CreateRoomModal from '@/domains/admin/CreateRoomModal/CreateRoomModal';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { AdminAuthData } from '@/types/adminAuth';
import { getLocalStorage } from '@/utils/localStorage';

const ADMIN = '관리자1';

export default function AdminHome() {
  const theme = useAppTheme();
  const adminName = getLocalStorage<AdminAuthData>('auth')?.adminName || ADMIN;

  const { openModal, closeModal, isOpen } = useModalContext();

  const handleCreateAdminOrganization = () => {
    openModal(<CreateRoomModal isOpen={isOpen} onClose={closeModal} />);
  };

  const { adminOrganizations, isLoading } = useAdminOrganization();

  return (
    <div css={homeLayout}>
      <AdminHomeHeader adminName={adminName} />
      <div css={adminOrganizationListContainer(theme)}>
        <div css={infoContainer}>
          <p css={listTitle(theme)}>피드백 방 목록</p>
          <p css={listCaption(theme)}>관리하고 있는 피드백 방들을 확인하세요</p>
        </div>
        <AdminOrganizationList
          adminOrganizations={adminOrganizations}
          isLoading={isLoading}
        />

        {!isLoading && (
          <FloatingButton
            icon={<PlusIcon color='white' width='24' height='24' />}
            onClick={handleCreateAdminOrganization}
            inset={{ bottom: '80px', left: '100%' }}
            customCSS={addAdminOrganization(theme)}
          />
        )}
      </div>
    </div>
  );
}
