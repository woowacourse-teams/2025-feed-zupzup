export const setupMockServiceWorker = () => {
  if (
    process.env.NODE_ENV === 'development' &&
    window.location.hostname === 'localhost'
  ) {
    (async () => {
      const { worker } = await import('../mocks/browser');
      await worker.start({
        onUnhandledRequest: 'bypass',
        serviceWorker: { url: '/mockServiceWorker.js' },
      });
      console.log('🛠️ MSW (Mock Service Worker) 시작됨');
    })();
  }
};
