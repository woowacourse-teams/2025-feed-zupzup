/* eslint-env browser, serviceworker */
/* global importScripts, firebase, self */

const CACHE_NAME = 'feed-zupzup-v1';
const urlsToCache = ['/', '/index.html'];
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
  // Firebase FCM 초기화 실패
}

if (self.location.hostname === 'localhost') {
  try {
    importScripts('/mockServiceWorker.js');
  } catch (error) {
    // MSW 로드 실패 (정상)
  }
}

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches
      .open(CACHE_NAME)
      .then((cache) => {
        return cache.addAll(urlsToCache);
      })
      .then(() => self.skipWaiting())
  );
});
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((cacheNames) => {
        return Promise.all(
          cacheNames.map((cacheName) => {
            if (cacheName !== CACHE_NAME) {
              return caches.delete(cacheName);
            }
          })
        );
      })
      .then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  const request = event.request;
  const url = new URL(request.url);

  const isApiRequest =
    url.hostname === 'localhost' ||
    url.hostname.startsWith('api-') ||
    url.hostname.startsWith('api.') ||
    request.headers.get('content-type')?.includes('application/json');

  if (isApiRequest) {
    event.respondWith(
      fetch(request)
        .then((response) => {
          if (
            response &&
            response.status === 200 &&
            response.type !== 'error'
          ) {
            const responseToCache = response.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(request, responseToCache);
            });
          }
          return response;
        })
        .catch(() => {
          return caches.match(request);
        })
    );
  } else {
    event.respondWith(
      caches.match(request).then((cachedResponse) => {
        if (cachedResponse) {
          return cachedResponse;
        }

        return fetch(request)
          .then((response) => {
            if (
              !response ||
              response.status !== 200 ||
              response.type === 'error'
            ) {
              return response;
            }

            if (request.method === 'GET') {
              const responseToCache = response.clone();
              caches.open(CACHE_NAME).then((cache) => {
                cache.put(request, responseToCache);
              });
            }

            return response;
          })
          .catch(() => {
            if (request.headers.get('accept')?.includes('text/html')) {
              return caches.match('/index.html');
            }
          });
      })
    );
  }
});
