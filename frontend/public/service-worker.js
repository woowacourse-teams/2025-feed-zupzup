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
    const title = (notification && notification.title) || '새 알림';
    const options = {
      body: (notification && notification.body) || '',
      icon: (notification && notification.icon) || '/logo192.png',
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
