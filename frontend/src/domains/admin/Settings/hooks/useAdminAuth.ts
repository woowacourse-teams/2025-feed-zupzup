import {
  AdminAuthData,
  getAdminAuth,
  AdminAuthResponse,
} from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

export default function useAdminAuth() {
  const [adminAuth, setAdminAuth] = useState<AdminAuthData | null>(null);
  const { showErrorModal } = useErrorModalContext();

  useEffect(() => {
    (async () => {
      try {
        await getAdminAuth({
          onSuccess: (response: AdminAuthResponse) =>
            setAdminAuth(response.data),
        });
      } catch (error) {
        showErrorModal(error as ApiError, '로그인 후 이용해주세요.');
        setAdminAuth(null);
      }
    })();
  }, []);

  return { adminAuth };
}
