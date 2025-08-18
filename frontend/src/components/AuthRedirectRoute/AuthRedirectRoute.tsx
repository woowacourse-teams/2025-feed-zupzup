import { ROUTES } from '@/constants';
import { ADMIN_BASE } from '@/constants/routes';
import { isAuthenticated } from '@/utils/isAuthenticated';
import { Navigate } from 'react-router-dom';

export default function AuthRedirectRoute({
  children,
}: {
  children: React.ReactNode;
}) {
  const authed = isAuthenticated();

  if (authed) {
    if (window.location.pathname === ROUTES.LOGIN) return <>{children}</>;
    return <Navigate to={ADMIN_BASE + ROUTES.ADMIN_HOME} replace />;
  }

  return <>{children}</>;
}
