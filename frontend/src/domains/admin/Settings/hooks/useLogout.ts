import { useErrorModalContext } from '@/contexts/useErrorModal';

export function useLogout() {
  const { showErrorModal } = useErrorModalContext();

  const handleLogout = async () => {
    try {
      // 여기에서 API 호출 예정
      // 로그인 화면으로 가는리다이렉트 로직도 추가할 예정
    } catch (e) {
      showErrorModal(e, '에러');
    }
  };

  return {
    handleLogout,
  };
}
