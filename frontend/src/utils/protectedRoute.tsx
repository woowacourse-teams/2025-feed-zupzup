import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { isAuthenticated } from '@/utils/authenticated';
import { ROUTES } from '@/constants';
import { useState, useEffect } from 'react';

interface ProtectedRouteProps {
  redirectPath?: string;
}

export default function ProtectedRoute({
  redirectPath = ROUTES.LOGIN,
}: ProtectedRouteProps) {
  const location = useLocation();
  const [authed, setAuthed] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      try {
        setAuthed(isAuthenticated());
      } catch (err) {
        console.error('Auth check failed:', err);
        setAuthed(false);
      }
    };
    checkAuth();
  }, []);

  if (!authed) {
    if (location.pathname === redirectPath) {
      return <Outlet />;
    }
    return <Navigate to={redirectPath} replace />;
  }

  return <Outlet />;
}
