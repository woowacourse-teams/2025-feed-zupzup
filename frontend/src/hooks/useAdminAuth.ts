import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

export const useAdminAuth = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [isAuthorized, setIsAuthorized] = useState(false);
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);

  useEffect(() => {
    const checkAdminAccess = () => {
      const isAuthenticated = sessionStorage.getItem('admin_auth');
      const password = searchParams.get('password');
      const adminPassword = process.env.ADMIN_PASSWORD || '';

      if (isAuthenticated === 'true') {
        setIsAuthorized(true);
      } else if (password === adminPassword) {
        sessionStorage.setItem('admin_auth', 'true');
        setIsAuthorized(true);
        navigate('/admin', { replace: true });
      } else {
        navigate('/', { replace: true });
        return;
      }
      if (location.pathname !== '/admin/login') {
        navigate('/admin/login', { replace: true });
      }

      setIsCheckingAuth(false);
    };

    checkAdminAccess();
  }, [searchParams, navigate]);

  return { isAuthorized, isCheckingAuth };
};
