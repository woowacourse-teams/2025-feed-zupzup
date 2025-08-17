/* eslint-env browser, serviceworker */
/* global importScripts, firebase, self */

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

  console.log('[ServiceWorker] Firebase FCM 초기화 완료');
} catch (error) {
  console.warn('[ServiceWorker] Firebase FCM 초기화 실패:', error);
}

if (self.location.hostname === 'localhost') {
  try {
    importScripts('/mockServiceWorker.js');
    console.log('[ServiceWorker] MSW 로드 완료');
  } catch (error) {
    console.warn('[ServiceWorker] MSW 로드 실패 (정상):', error);
  }
}

self.addEventListener('install', (event) => {
  console.log('[ServiceWorker] Installed');
  event.waitUntil(self.skipWaiting());
});

self.addEventListener('activate', (event) => {
  console.log('[ServiceWorker] Activated');
  event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
  event.respondWith(fetch(event.request));
});
