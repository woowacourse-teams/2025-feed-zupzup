import { AdminOrganizationType } from '@/apis/adminOrganization.api';
import { loadingContainer } from '@/components/TimeDelayModal/TimeDelayModal.styles';
import AdminOrganization from '@/domains/admin/AdminHome/components/AdminOrganization/AdminOrganization';
import {
  adminOrganizationList,
  adminSpinner,
  emptyAdminOrganization,
} from '@/domains/admin/AdminHome/components/AdminOrganizationList/AdminOrganizationList.style';
import StatusBox from '@/domains/components/StatusBox/StatusBox';
import { useNavigate } from 'react-router-dom';

interface AdminOrganizationListProps {
  adminOrganizations: AdminOrganizationType[];
  isLoading: boolean;
}

export default function AdminOrganizationList({
  adminOrganizations,
  isLoading,
}: AdminOrganizationListProps) {
  const navigate = useNavigate();

  if (isLoading) {
    return (
      <div css={loadingContainer}>
        <div css={adminSpinner} />
        데이터를 불러오는 중입니다...
      </div>
    );
  }

  return (
    <div css={adminOrganizationList}>
      {adminOrganizations.length === 0 ? (
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
        adminOrganizations.map((organizations) => (
          <AdminOrganization
            key={organizations.uuid}
            organizationName={organizations.name}
            waitingCount={organizations.waitingCount}
            postedAt={organizations.postedAt}
            onClick={() => navigate(`/admin/${organizations.uuid}/dashboard`)}
          />
        ))
      )}
    </div>
  );
}
