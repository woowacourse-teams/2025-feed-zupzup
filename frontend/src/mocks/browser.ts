import { setupWorker } from 'msw/browser';
import { handler } from '@/mocks/handler';

export const worker = setupWorker(...handler);

export const startMSW = async () => {
  if (
    process.env.NODE_ENV === 'development' &&
    window.location.protocol === 'http:'
  ) {
    try {
      await worker.start({
        serviceWorker: {
          url: '/firebase-messaging-sw.js', // 통합된 Service Worker 사용
        },
        onUnhandledRequest: 'bypass',
      });
      console.log('[MSW] 목업 서버 시작됨');
    } catch (error) {
      console.warn(
        '[MSW] Service Worker 등록 실패, MSW는 비활성화됩니다:',
        error
      );
    }
  } else {
    console.log('[MSW] HTTPS 환경이므로 MSW를 비활성화합니다.');
  }
};
