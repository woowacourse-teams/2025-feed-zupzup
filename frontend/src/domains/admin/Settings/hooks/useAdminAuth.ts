import { AdminAuthResponse, getAdminAuth } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { AdminAuthData } from '@/types/adminAuth';
import { useEffect, useState } from 'react';

export default function useAdminAuth() {
  const [adminAuth, setAdminAuth] = useState<AdminAuthData | null>(null);
  const { handleApiError } = useApiErrorHandler();

  useEffect(() => {
    (async () => {
      try {
        await getAdminAuth({
          onSuccess: (response: AdminAuthResponse) =>
            setAdminAuth(response.data),
        });
      } catch (error) {
        handleApiError(error as ApiError);
        setAdminAuth(null);
      }
    })();
  }, []);

  return { adminAuth };
}
