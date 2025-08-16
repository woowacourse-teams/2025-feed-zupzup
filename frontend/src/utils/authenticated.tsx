import { ROUTES } from '@/constants';
import { ADMIN_BASE } from '@/constants/routes';
import React from 'react';
import { Navigate } from 'react-router-dom';

export const isAuthenticated = () => {
  const auth = localStorage.getItem('auth');
  return auth !== null;
};

export const getAuthRedirectElement = (Component: React.ReactNode) => {
  return isAuthenticated() ? (
    <Navigate to={ADMIN_BASE + ROUTES.ADMIN_HOME} replace />
  ) : (
    Component
  );
};
