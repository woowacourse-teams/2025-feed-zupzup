import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useState } from 'react';

export function useLogout() {
  const { showErrorModal } = useErrorModalContext();
  const [isLoading, setIsLoading] = useState(false);

  const handleLogout = async () => {
    setIsLoading(true);
    try {
      // 여기에서 API 호출 예정
      // 로그인 화면으로 가는리다이렉트 로직도 추가할 예정
    } catch (e) {
      showErrorModal(e, '에러');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    handleLogout,
    isLoading,
  };
}
