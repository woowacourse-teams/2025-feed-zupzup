/* eslint-env browser, serviceworker */
/* global importScripts, self */

// Completely disable Workbox logger
self.__WB_DISABLE_DEV_LOGS = true;

const originalLog = console.log;
console.log = function (...args) {
  const message = args[0];
  if (typeof message === 'string' && message.includes('workbox')) return;
  if (message?.includes?.('workbox')) return;
  originalLog.apply(console, args);
};

import { precacheAndRoute } from 'workbox-precaching';
import { registerRoute, NavigationRoute } from 'workbox-routing';
import {
  NetworkFirst,
  CacheFirst,
  StaleWhileRevalidate,
  NetworkOnly,
} from 'workbox-strategies';
import { ExpirationPlugin } from 'workbox-expiration';
import { CacheableResponsePlugin } from 'workbox-cacheable-response';
import { BackgroundSyncPlugin } from 'workbox-background-sync';
import { BroadcastUpdatePlugin } from 'workbox-broadcast-update';

// ===== Configuration =====
const CACHE_VERSION = 'v6';

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

// ===== MSW Setup (개발 환경) =====
if (self.location.hostname === 'localhost') {
  self.addEventListener('fetch', (event) => {
    if (event.request.url.includes('mockServiceWorker')) {
      return;
    }
  });
}

// ===== Precaching =====
precacheAndRoute(
  self.__WB_MANIFEST || [
    { url: '/', revision: CACHE_VERSION },
    { url: '/index.html', revision: CACHE_VERSION },
  ]
);

// ===== Never Cache Routes =====
registerRoute(({ url }) => {
  return (
    url.pathname.includes('service-worker.js') ||
    url.pathname.includes('mockServiceWorker.js') ||
    url.href.includes('.hot-update') ||
    url.href.includes('sockjs-node') ||
    url.href.includes('webpack')
  );
}, new NetworkOnly());

// ===== HTML/JS/CSS - Network First =====

registerRoute(
  ({ url }) => {
    return (
      url.pathname.endsWith('.html') ||
      url.pathname.endsWith('.js') ||
      url.pathname.endsWith('.css') ||
      url.pathname.endsWith('.chunk.js') ||
      url.pathname.includes('manifest.json')
    );
  },
  new NetworkFirst({
    cacheName: `app-shell-cache-${CACHE_VERSION}`,
    networkTimeoutSeconds: 3,
    plugins: [
      new CacheableResponsePlugin({
        statuses: [0, 200],
      }),
      new ExpirationPlugin({
        maxEntries: 50,
        maxAgeSeconds: 7 * 24 * 60 * 60,
      }),
    ],
  })
);

// ===== 온보딩 페이지 - 항상 네트워크 우선 =====
registerRoute(
  ({ url }) => {
    return url.pathname === '/' || url.pathname === '/index.html';
  },
  new NetworkFirst({
    cacheName: `onboarding-cache-${CACHE_VERSION}`,
    networkTimeoutSeconds: 1,
    plugins: [
      new CacheableResponsePlugin({
        statuses: [0, 200],
      }),
      new ExpirationPlugin({
        maxEntries: 5,
        maxAgeSeconds: 60,
      }),
    ],
  })
);

// ===== Images & Fonts - Cache First =====
registerRoute(
  ({ request, url }) => {
    return (
      request.destination === 'image' ||
      request.destination === 'font' ||
      /\.(png|jpg|jpeg|gif|svg|webp|ico|woff|woff2|ttf|eot)$/i.test(
        url.pathname
      )
    );
  },
  new CacheFirst({
    cacheName: `static-assets-cache-${CACHE_VERSION}`,
    plugins: [
      new CacheableResponsePlugin({
        statuses: [0, 200],
      }),
      new ExpirationPlugin({
        maxEntries: 100,
        maxAgeSeconds: 30 * 24 * 60 * 60,
        purgeOnQuotaError: true,
      }),
    ],
  })
);

// ===== API Routes Configuration =====
const isApiRequest = (url) => {
  return (
    (url.hostname === 'localhost' && url.pathname.startsWith('/api/')) ||
    url.hostname.startsWith('api-') ||
    url.hostname.startsWith('api.') ||
    url.hostname.endsWith('feedzupzup.com') ||
    url.pathname.startsWith('/api/')
  );
};

// ===== Auth Routes - Immediate Failure (Background Sync 제거) =====
registerRoute(({ url }) => {
  return (
    isApiRequest(url) &&
    (url.pathname.includes('/admin/login') ||
      url.pathname.includes('/admin/logout') ||
      url.pathname.includes('/admin/sign-up') ||
      url.pathname.includes('/admin/me'))
  );
}, new NetworkOnly());

registerRoute(
  ({ url, request }) => {
    return (
      isApiRequest(url) &&
      request.method === 'GET' &&
      (url.pathname.includes('/feedbacks') ||
        url.pathname.includes('/notifications') ||
        url.pathname.includes('/organizations'))
    );
  },
  new NetworkFirst({
    cacheName: `realtime-api-cache-${CACHE_VERSION}`,
    networkTimeoutSeconds: 5,
    plugins: [
      new CacheableResponsePlugin({
        statuses: [0, 200],
        headers: {
          'X-Cache-Control': 'public',
        },
      }),
      new ExpirationPlugin({
        maxEntries: 50,
        maxAgeSeconds: 5 * 60,
      }),
      new BroadcastUpdatePlugin({
        headersToCheck: ['X-Last-Modified', 'ETag'],
      }),
    ],
  })
);

registerRoute(
  ({ url, request }) => {
    return isApiRequest(url) && request.method === 'GET';
  },
  new StaleWhileRevalidate({
    cacheName: `api-cache-${CACHE_VERSION}`,
    plugins: [
      new CacheableResponsePlugin({
        statuses: [0, 200],
      }),
      new ExpirationPlugin({
        maxEntries: 100,
        maxAgeSeconds: 60 * 60,
        purgeOnQuotaError: true,
      }),
      new BroadcastUpdatePlugin(),
    ],
  })
);

// ===== Background Sync for Critical Mutations =====
const criticalMutationsBgSync = new BackgroundSyncPlugin('critical-mutations', {
  maxRetentionTime: 24 * 60,
  onSync: async ({ queue }) => {
    let entry;
    while ((entry = await queue.shiftRequest())) {
      try {
        const response = await fetch(entry.request.clone());

        if (response.ok) {
          const url = new URL(entry.request.url);
          const cachesToUpdate = [
            `realtime-api-cache-${CACHE_VERSION}`,
            `api-cache-${CACHE_VERSION}`,
          ];

          for (const cacheName of cachesToUpdate) {
            const cache = await caches.open(cacheName);
            const keys = await cache.keys();

            const keysToDelete = keys.filter((key) => {
              const keyUrl = new URL(key.url);
              const keyPathname = keyUrl.pathname;

              const isFeedbackRelated =
                /\/admin\/feedbacks/.test(keyPathname) ||
                /\/organizations\/\d+\/feedbacks/.test(keyPathname) ||
                /\/feedbacks\/\d+\/(like|unlike)/.test(keyPathname) ||
                /\/admin\/feedbacks\/statistics/.test(keyPathname);

              const isOrganizationRelated =
                /\/admin\/organizations/.test(keyPathname) ||
                /\/organizations\/\d+/.test(keyPathname);

              const isSameResource = keyUrl.pathname.includes(
                url.pathname.split('/').slice(0, -1).join('/')
              );

              return (
                isFeedbackRelated || isOrganizationRelated || isSameResource
              );
            });

            await Promise.all(keysToDelete.map((key) => cache.delete(key)));
          }

          const channel = new BroadcastChannel('api-updates');
          channel.postMessage({
            type: 'CACHE_INVALIDATED',
            url: entry.request.url,
            method: entry.request.method,
          });
        }

        return response;
      } catch (error) {
        await queue.unshiftRequest(entry);
        throw error;
      }
    }
  },
});

// ===== Critical Mutations with Background Sync (피드백 작성, 좋아요, 응원하기) =====
registerRoute(
  ({ url, request }) => {
    if (!isApiRequest(url)) return false;

    const pathname = url.pathname;

    const isFeedbackCreation =
      request.method === 'POST' &&
      /\/organizations\/\d+\/feedbacks$/.test(pathname);

    const isLike = /\/feedbacks\/\d+\/(like|unlike)$/.test(pathname);

    const isCheer = /\/feedbacks\/\d+\/cheer$/.test(pathname);

    return isFeedbackCreation || isLike || isCheer;
  },
  new NetworkOnly({
    plugins: [criticalMutationsBgSync],
  })
);

// ===== Other Mutations (Background Sync 없이) =====
registerRoute(({ url, request }) => {
  return (
    isApiRequest(url) &&
    ['POST', 'PUT', 'PATCH', 'DELETE'].includes(request.method)
  );
}, new NetworkOnly());

// ===== Navigation Route (SPA) =====
const navigationRoute = new NavigationRoute(
  new NetworkFirst({
    cacheName: `navigation-cache-${CACHE_VERSION}`,
    plugins: [
      new CacheableResponsePlugin({
        statuses: [0, 200],
      }),
    ],
  })
);

registerRoute(navigationRoute);

// ===== Skip Waiting & Clients Claim =====
self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    (async () => {
      const cacheNames = await caches.keys();
      const oldCaches = cacheNames.filter(
        (name) => !name.includes(CACHE_VERSION) && !name.startsWith('workbox-')
      );
      await Promise.all(oldCaches.map((name) => caches.delete(name)));

      await clients.claim();
    })()
  );
});
