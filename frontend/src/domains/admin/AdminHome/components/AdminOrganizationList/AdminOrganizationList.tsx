import AdminOrganization from '@/domains/admin/AdminHome/components/AdminOrganization/AdminOrganization';
import { adminOrganizationList } from '@/domains/admin/AdminHome/components/AdminOrganizationList/AdminOrganizationList.style';
import useAdminOrganization from '@/domains/admin/AdminHome/hooks/useAdminOrganization';
import { useNavigate } from 'react-router-dom';

export default function AdminOrganizationList() {
  const navigate = useNavigate();

  const { adminOrganizations } = useAdminOrganization();

  return (
    <div css={adminOrganizationList}>
      {adminOrganizations.map((organizations) => (
        <AdminOrganization
          key={organizations.uuid}
          organizationName={organizations.name}
          waitingCount={organizations.waitingCount}
          postedAt={organizations.postedAt}
          onClick={() => navigate(`/admin/${organizations.uuid}/dashboard`)}
        />
      ))}
    </div>
  );
}
