/* eslint-env browser, serviceworker */
/* global importScripts, self */

// ===== Firebase Setup =====
try {
  importScripts(
    'https://www.gstatic.com/firebasejs/9.23.0/firebase-app-compat.js'
  );
  importScripts(
    'https://www.gstatic.com/firebasejs/9.23.0/firebase-messaging-compat.js'
  );

  firebase.initializeApp({
    apiKey: 'AIzaSyC-GvMd4MkZ2T4rRHc5cKPH1KAB5yeDUL8',
    authDomain: 'feedzupzup-ba753.firebaseapp.com',
    projectId: 'feedzupzup-ba753',
    storageBucket: 'feedzupzup-ba753.firebasestorage.app',
    messagingSenderId: '1001684234371',
    appId: '1:1001684234371:web:f52cb8add92937d800abb6',
  });

  const messaging = firebase.messaging();

  messaging.onBackgroundMessage(({ notification, data }) => {
    const title = (data && data.title) || '새 알림';
    const options = {
      body: (data && data.body) || '',
      icon: (data && data.icon) || '/192x192.png',
      data,
      requireInteraction: true,
    };
    self.registration.showNotification(title, options);
  });
} catch (error) {
  console.error('Firebase messaging 초기화 실패:', error);
}

// ===== Install - 모든 캐시 삭제 및 즉시 활성화 =====
self.addEventListener('install', (event) => {
  event.waitUntil(
    (async () => {
      const cacheNames = await caches.keys();
      await Promise.all(cacheNames.map((name) => caches.delete(name)));
    })()
  );
  self.skipWaiting();
});

// ===== Activate - 모든 캐시 삭제 및 클라이언트 제어 =====
self.addEventListener('activate', (event) => {
  event.waitUntil(
    (async () => {
      const cacheNames = await caches.keys();
      await Promise.all(cacheNames.map((name) => caches.delete(name)));
      await clients.claim();
    })()
  );
});

self.addEventListener('fetch', (event) => {
  if (event.request.url.includes('mockServiceWorker')) {
    return;
  }

  event.respondWith(fetch(event.request));
});

self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

// ===== PWA 알림 클릭 이벤트 처리 =====
self.addEventListener('notificationclick', (event) => {
  event.notification.close();

  event.waitUntil(
    clients
      .matchAll({ type: 'window', includeUncontrolled: true })
      .then((clientList) => {
        for (const client of clientList) {
          if (
            client.url.startsWith(self.location.origin) &&
            'focus' in client
          ) {
            return client.focus().then(() => client.navigate('/'));
          }
        }
        return clients.openWindow('/');
      })
  );
});
