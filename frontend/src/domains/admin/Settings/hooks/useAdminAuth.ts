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

  const handleError = (error: ApiError) => {
    showErrorModal(error, '로그인 후 이용해주세요.');
    setAdminAuth(null);
  };

  useEffect(() => {
    (async () => {
      try {
        await getAdminAuth({
          onError: () =>
            handleError(new ApiError(401, '관리자 인증 정보 조회 실패')),
          onSuccess: (response: AdminAuthResponse) =>
            setAdminAuth(response.data),
        });
      } catch (error) {
        handleError(error as ApiError);
      }
    })();
  }, []);

  return { adminAuth };
}
