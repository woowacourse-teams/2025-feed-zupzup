export const registerServiceWorker = () => {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker
        .register('/service-worker.js')
        .then((reg) => {
          console.log('Service Worker registered:', reg);

          reg.addEventListener('updatefound', () => {
            console.log('🔄 새로운 Service Worker가 감지되었습니다.');
            const newWorker = reg.installing;

            if (newWorker) {
              newWorker.addEventListener('statechange', () => {
                if (
                  newWorker.state === 'installed' &&
                  navigator.serviceWorker.controller
                ) {
                  console.log(
                    '✅ 새로운 콘텐츠가 사용 가능합니다. 페이지를 새로고침하세요.'
                  );
                }
              });
            }
          });
        })
        .catch((err) =>
          console.log('Service Worker registration failed:', err)
        );
    });
  }
};
