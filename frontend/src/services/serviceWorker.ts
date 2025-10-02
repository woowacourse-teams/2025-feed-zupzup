export const registerServiceWorker = () => {
  console.log(process.env.ENV_MODE);
  if (process.env.ENV_MODE === 'local') {
    console.log('[ServiceWorker] Local mode - skipping registration');
    return;
  }

  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker
        .register('/service-worker.js')
        .then((reg) => console.log('Service Worker registered:', reg))
        .catch((err) =>
          console.log('Service Worker registration failed:', err)
        );
    });
  }
};
