import { isAuthenticated } from '@/utils/authenticated';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

interface ProtectedRouteProps {
  redirectPath?: string;
}

export default function ProtectedRoute({
  redirectPath = '/login',
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
