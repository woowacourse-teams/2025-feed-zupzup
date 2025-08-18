import { ROUTES } from '@/constants';
import { ADMIN_BASE } from '@/constants/routes';
import React from 'react';
import { Navigate } from 'react-router-dom';

export const isAuthenticated = () => {
  try {
    const auth = localStorage.getItem('auth');
    if (!auth) return false;

    const parsedAuth = JSON.parse(auth);
    return Boolean(
      parsedAuth?.adminId && parsedAuth?.adminName && parsedAuth?.loginId
    );
  } catch {
    localStorage.removeItem('auth');
    return false;
  }
};

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
