import { loadingContainer } from '@/components/TimeDelayModal/TimeDelayModal.styles';
import AdminOrganization from '@/domains/admin/AdminHome/components/AdminOrganization/AdminOrganization';
import {
  adminOrganizationList,
  adminSpinner,
  emptyAdminOrganization,
} from '@/domains/admin/AdminHome/components/AdminOrganizationList/AdminOrganizationList.style';
import StatusBox from '@/domains/components/StatusBox/StatusBox';
import useNavigation from '@/domains/hooks/useNavigation';
import useAdminOrganization from '../../hooks/useAdminOrganization';
import { AdminAuthData } from '@/types/adminAuth';
import { getLocalStorage } from '@/utils/localStorage';
import FloatingButton from '@/domains/components/FloatingButton/FloatingButton';
import PlusIcon from '@/components/icons/PlusIcon';
import { addAdminOrganization } from '../../AdminHome.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useModalContext } from '@/contexts/useModal';
import CreateRoomModal from '@/domains/admin/CreateRoomModal/CreateRoomModal';

const ADMIN = '관리자1';

export default function AdminOrganizationList() {
  const theme = useAppTheme();
  const adminName = getLocalStorage<AdminAuthData>('auth')?.adminName || ADMIN;
  const { adminOrganizations, isLoading } = useAdminOrganization({ adminName });

  const { goPath } = useNavigation();

  const { openModal, closeModal } = useModalContext();

  const handleCreateAdminOrganization = () => {
    openModal(<CreateRoomModal onClose={closeModal} />);
  };

  if (isLoading) {
    return (
      <div css={loadingContainer} role='status' aria-live='polite'>
        <div css={adminSpinner} />
        데이터를 불러오는 중입니다...
      </div>
    );
  }

  const organizationCount = adminOrganizations.length;
  const ariaLabel =
    organizationCount === 0
      ? '등록된 피드백 방이 없습니다.'
      : `총 ${organizationCount}개의 피드백 방이 있습니다.`;

  return (
    <div>
      <div
        role='region'
        aria-label={ariaLabel}
        tabIndex={0}
        className='srOnly'
      />
      {organizationCount === 0 ? (
        <div css={emptyAdminOrganization}>
          <StatusBox
            width={'100%'}
            height={'200px'}
            textIcon='🏘️'
            title=' 등록된 조직이 없습니다.'
            description='+ 버튼을 눌러 조직을 추가해주세요'
          />
        </div>
      ) : (
        <ul role='list' css={adminOrganizationList}>
          {adminOrganizations.map((organizations) => (
            <li key={organizations.uuid}>
              <AdminOrganization
                organizationName={organizations.name}
                waitingCount={organizations.waitingCount}
                postedAt={organizations.postedAt}
                onClick={() => goPath(`/admin/${organizations.uuid}/dashboard`)}
              />
            </li>
          ))}
        </ul>
      )}

      {!isLoading && (
        <FloatingButton
          icon={<PlusIcon color='white' width='24' height='24' />}
          onClick={handleCreateAdminOrganization}
          inset={{ bottom: '80px', left: '100%' }}
          customCSS={addAdminOrganization(theme)}
          aria-label='새 피드백 방 추가'
        />
      )}
    </div>
  );
}
