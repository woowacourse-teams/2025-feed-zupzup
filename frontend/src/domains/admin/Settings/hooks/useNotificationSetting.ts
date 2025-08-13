import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useState } from 'react';

export function useNotificationSetting() {
  const { showErrorModal } = useErrorModalContext();
  const [isToggleEnabled, setIsToggleEnabled] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const updateNotificationSetting = async (enabled: boolean) => {
    setIsLoading(true);
    try {
      // 요기서 API 호출 예정
      setIsToggleEnabled(enabled);
    } catch (e) {
      showErrorModal(e, '에러');
    } finally {
      setIsLoading(false);
    }
  };

  return {
    isToggleEnabled,
    isLoading,
    updateNotificationSetting,
  };
}
