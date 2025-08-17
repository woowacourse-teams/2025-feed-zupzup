import { ROUTES } from '@/constants';
import { ADMIN_BASE } from '@/constants/routes';
import { AdminAuthData } from '@/types/adminAuth';
import React from 'react';
import { Navigate } from 'react-router-dom';

export const isAuthenticated = () => {
  const auth = localStorage.getItem('auth');

  if (!auth) {
    return false;
  }

  try {
    const parsedAuth = JSON.parse(auth) as Partial<AdminAuthData>;
    return Boolean(
      parsedAuth?.adminId && parsedAuth?.adminName && parsedAuth?.loginId
    );
  } catch (error) {
    console.error('Error parsing auth data:', error);
    return false;
  }
};

export const getAuthRedirectElement = (Component: React.ReactNode) => {
  return isAuthenticated() ? (
    <Navigate to={ADMIN_BASE + ROUTES.ADMIN_HOME} replace />
  ) : (
    Component
  );
};
