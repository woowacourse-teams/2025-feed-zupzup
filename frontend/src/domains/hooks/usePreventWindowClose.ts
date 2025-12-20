import { useEffect } from 'react';

interface UsePreventWindowCloseProps {
  isDownloading: boolean;
}
export default function usePreventWindowClose({
  isDownloading,
}: UsePreventWindowCloseProps) {
  useEffect(() => {
    if (!isDownloading) return;

    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = ''; // 일부 브라우저에서 필요
      return '';
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [isDownloading]);
}
