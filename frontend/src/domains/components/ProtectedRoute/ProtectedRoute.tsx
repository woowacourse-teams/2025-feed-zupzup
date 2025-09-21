import { isAuthenticated } from '@/utils/isAuthenticated';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

interface ProtectedRouteProps {
  redirectPath?: string;
}

export default function ProtectedRoute({
  redirectPath = '/',
}: ProtectedRouteProps) {
  const location = useLocation();
  const authed = isAuthenticated();

  if (!authed) {
    if (location.pathname === redirectPath) {
      return null;
    }
    return <Navigate to={redirectPath} replace />;
  }

  return <Outlet />;
}
