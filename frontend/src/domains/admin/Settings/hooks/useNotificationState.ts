import { useState, useEffect } from 'react';
import {
  getStoredNotificationState,
  setStoredNotificationState,
} from '@/utils/notificationUtils';

export const useNotificationState = (organizationId: number) => {
  const [isEnabled, setIsEnabled] = useState(() =>
    getStoredNotificationState(organizationId)
  );

  useEffect(() => {
    setIsEnabled(getStoredNotificationState(organizationId));
  }, [organizationId]);

  const updateState = (enabled: boolean) => {
    setIsEnabled(enabled);
    setStoredNotificationState(organizationId, enabled);
  };

  return {
    isEnabled,
    updateState,
  };
};
