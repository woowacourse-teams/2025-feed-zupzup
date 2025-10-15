/* eslint-env browser, serviceworker */
/* global importScripts, firebase, self */

// ===== Configuration =====
const CACHE_VERSION = 'v2';
const CACHE_NAME = `feed-zupzup-${CACHE_VERSION}`;
const urlsToCache = ['/', '/index.html'];

const NEVER_CACHE = ['service-worker.js', 'mockServiceWorker.js'];

const NETWORK_FIRST = ['manifest.json'];
const NETWORK_FIRST_PATTERNS = [/\.html$/, /\.css$/, /\.js$/, /\.chunk\.js$/];

const CACHE_FIRST_PATTERNS = [
  /\.(png|jpg|jpeg|gif|svg|webp|ico)$/,
  /\.(woff|woff2|ttf|eot)$/,
];

const NEVER_CACHE_PATTERNS = [/\.(hot-update)\./, /sockjs-node/, /webpack/];

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

// ===== MSW Setup =====
if (self.location.hostname === 'localhost') {
  try {
    importScripts('/mockServiceWorker.js');
  } catch (error) {
    console.error('MSW 로드 실패:', error);
  }
}

// ===== Cache Helpers =====
function shouldNeverCache(url) {
  if (NEVER_CACHE.some((pattern) => url.pathname.includes(pattern))) {
    return true;
  }
  if (NEVER_CACHE_PATTERNS.some((pattern) => pattern.test(url.href))) {
    return true;
  }
  return false;
}

function shouldNetworkFirst(url) {
  if (NETWORK_FIRST.some((pattern) => url.pathname.includes(pattern))) {
    return true;
  }
  if (NETWORK_FIRST_PATTERNS.some((pattern) => pattern.test(url.href))) {
    return true;
  }
  return false;
}

function shouldCacheFirst(url) {
  return CACHE_FIRST_PATTERNS.some((pattern) => pattern.test(url.href));
}

// ===== Lifecycle Events =====
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

  if (url.origin !== location.origin) {
    return;
  }

  if (request.method !== 'GET') {
    return;
  }

  // Never Cache
  if (shouldNeverCache(url)) {
    event.respondWith(
      fetch(request).catch(() => {
        return new Response('', { status: 408, statusText: 'Request Timeout' });
      })
    );
    return;
  }

  // Network First
  if (shouldNetworkFirst(url)) {
    event.respondWith(
      fetch(request)
        .then((response) => {
          if (response && response.status === 200) {
            const responseToCache = response.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(request, responseToCache);
            });
          }
          return response;
        })
        .catch(() => {
          return caches.match(request).then((cached) => {
            if (cached) {
              return cached;
            }

            if (request.headers.get('accept')?.includes('text/html')) {
              return caches.match('/index.html');
            }

            return new Response('', {
              status: 503,
              statusText: 'Service Unavailable',
            });
          });
        })
    );
    return;
  }

  // API Request
  const isApiRequest =
    url.hostname === 'localhost' ||
    url.hostname.startsWith('api-') ||
    url.hostname.startsWith('api.') ||
    url.pathname.startsWith('/api/') ||
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
          return caches.match(request).then((cached) => {
            if (cached) {
              return cached;
            }

            return new Response(
              JSON.stringify({
                error: 'Service Unavailable',
                message: '네트워크 연결을 확인해주세요',
              }),
              {
                status: 503,
                statusText: 'Service Unavailable',
                headers: { 'Content-Type': 'application/json' },
              }
            );
          });
        })
    );
    return;
  }

  // Cache First
  if (shouldCacheFirst(url)) {
    event.respondWith(
      caches.match(request).then((cachedResponse) => {
        if (cachedResponse) {
          return cachedResponse;
        }

        return fetch(request)
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
            return new Response(new Blob([]), {
              status: 200,
              headers: { 'Content-Type': 'image/png' },
            });
          });
      })
    );
    return;
  }

  // Default Strategy
  event.respondWith(
    fetch(request)
      .then((response) => {
        if (response && response.status === 200 && response.type !== 'error') {
          const responseToCache = response.clone();
          caches.open(CACHE_NAME).then((cache) => {
            cache.put(request, responseToCache);
          });
        }
        return response;
      })
      .catch(() => {
        return caches.match(request).then((cachedResponse) => {
          if (cachedResponse) {
            return cachedResponse;
          }
          if (request.headers.get('accept')?.includes('text/html')) {
            return caches.match('/index.html');
          }

          return new Response('', {
            status: 503,
            statusText: 'Service Unavailable',
          });
        });
      })
  );
});
