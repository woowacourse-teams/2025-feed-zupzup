import { isAuthenticated } from '@/utils/authenticated';
import { Navigate, Outlet } from 'react-router-dom';

interface ProtectedRouteProps {
  redirectPath?: string;
}

export default function ProtectedRoute({
  redirectPath = '/login',
}: ProtectedRouteProps) {
  return isAuthenticated() ? (
    <Outlet />
  ) : (
    <Navigate to={redirectPath} replace />
  );
}
