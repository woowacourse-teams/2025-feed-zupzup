import { useCallback, useSyncExternalStore } from 'react';
import { toastStore } from './toastStore';
import { ToastType } from '@/contexts/useToast';

export function useToastActions() {
  const showToast = useCallback(
    (message: string, type: ToastType = 'error', duration: number = 3000) => {
      toastStore.setState({ toastData: { message, type, duration } });
    },
    []
  );

  const hideToast = useCallback(() => {
    toastStore.setState({ toastData: null });
  }, []);
  return { showToast, hideToast };
}

export function useToastState() {
  const { toastData } = useSyncExternalStore(
    toastStore.subscribe,
    toastStore.getSnapshot
  );

  return { toastData };
}
