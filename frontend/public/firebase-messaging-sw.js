/* eslint-env serviceworker */
/* global importScripts, firebase, self */

importScripts(
  'https://www.gstatic.com/firebasejs/9.23.0/firebase-app-compat.js'
);
importScripts(
  'https://www.gstatic.com/firebasejs/9.23.0/firebase-messaging-compat.js'
);

// 공개 firebaseConfig (Web SDK 설정)
firebase.initializeApp({
  apiKey: 'AIzaSyC-GvMd4MkZ2T4rRHc5cKPH1KAB5yeDUL8',
  authDomain: 'feedzupzup-ba753.firebaseapp.com',
  projectId: 'feedzupzup-ba753',
  storageBucket: 'feedzupzup-ba753.firebasestorage.app',
  messagingSenderId: '1001684234371',
  appId: '1:1001684234371:web:f52cb8add92937d800abb6',
});

const messaging = firebase.messaging();

// 백그라운드 메시지 → 알림 표시
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

// 개발환경에서 MSW 워커 스크립트 주입(루트 경로에 있어야 함)
if (self.location.hostname === 'localhost') {
  importScripts('/mockServiceWorker.js');
}

// 빠른 활성화
self.addEventListener('install', (e) => e.waitUntil(self.skipWaiting()));
self.addEventListener('activate', (e) => e.waitUntil(self.clients.claim()));
