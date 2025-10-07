import { QUERY_KEYS } from '@/constants/queryKeys';
import {
  adminOrganizationListContainer,
  homeLayout,
  infoContainer,
  listCaption,
  listTitle,
} from '@/domains/admin/AdminHome/AdminHome.style';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import AdminOrganizationList from '@/domains/admin/AdminHome/components/AdminOrganizationList/AdminOrganizationList';
import LocalErrorBoundary from '@/error/LocalError/LocalErrorBoundary';
import LocalErrorFallback from '@/error/LocalError/LocalErrorFallback';
import { useAppTheme } from '@/hooks/useAppTheme';
import { AdminAuthData } from '@/types/adminAuth';
import { getLocalStorage } from '@/utils/localStorage';

const ADMIN = '관리자1';

export default function AdminHome() {
  const theme = useAppTheme();
  const adminName = getLocalStorage<AdminAuthData>('auth')?.adminName || ADMIN;

  return (
    <div css={homeLayout}>
      <AdminHomeHeader adminName={adminName} />
      <div css={adminOrganizationListContainer(theme)}>
        <div css={infoContainer}>
          <p css={listTitle(theme)}>피드백 방 목록</p>
          <p css={listCaption(theme)}>관리하고 있는 피드백 방들을 확인하세요</p>
        </div>
        <LocalErrorBoundary
          fallback={LocalErrorFallback}
          queryKey={QUERY_KEYS.adminOrganizations(adminName)}
        >
          <AdminOrganizationList />
        </LocalErrorBoundary>
      </div>
    </div>
  );
}
