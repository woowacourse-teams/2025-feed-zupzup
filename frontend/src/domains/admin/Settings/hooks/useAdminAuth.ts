import { AdminAuthData, getAdminAuth } from '@/apis/admin.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

export default function useAdminAuth() {
  const [adminAuth, setAdminAuth] = useState<AdminAuthData | null>(null);
  const { showErrorModal } = useErrorModalContext();

  const handleError = (error: Error) => {
    showErrorModal(error, '로그인 후 이용해주세요.');
    setAdminAuth(null);
  };

  useEffect(() => {
    (async () => {
      try {
        await getAdminAuth({
          onError: () => handleError(new Error('관리자 인증 정보 조회 실패')),
          onSuccess: (data) => setAdminAuth(data),
        });
      } catch (error) {
        handleError(error as Error);
      }
    })();
  }, []);

  return { adminAuth };
}
