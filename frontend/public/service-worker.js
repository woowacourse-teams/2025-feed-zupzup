/* eslint-env browser, serviceworker */
/* global importScripts, self */

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
const CACHE_VERSION = 'v3';

// ===== Firebase Setup =====
// Firebase는 Workbox와 별도로 동작하므로 그대로 유지
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
    // 치명 이슈 수정 #1: 캐시 이름에 버전 포함
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
    // 치명 이슈 수정 #3: localhost도 /api/ 프리픽스일 때만 API로 취급
    (url.hostname === 'localhost' && url.pathname.startsWith('/api/')) ||
    url.hostname.startsWith('api-') ||
    url.hostname.startsWith('api.') ||
    url.hostname.endsWith('feedzupzup.com') ||
    url.pathname.startsWith('/api/')
  );
};

// 1. 민감/개인 영역 - NetworkOnly (+BG Sync 큐는 유지)
registerRoute(
  ({ url }) => {
    return (
      isApiRequest(url) &&
      (url.pathname.includes('/user/profile') ||
        url.pathname.includes('/user/settings') ||
        url.pathname.includes('/auth') ||
        url.pathname.includes('/private'))
    );
  },
  new NetworkOnly({
    plugins: [
      new BackgroundSyncPlugin('private-api-queue', {
        maxRetentionTime: 24 * 60,
      }),
    ],
  })
);

// 2. 실시간성이 중요한 API - Network First (짧은 캐시)
registerRoute(
  ({ url, request }) => {
    return (
      isApiRequest(url) &&
      request.method === 'GET' &&
      (url.pathname.includes('/feedbacks') ||
        url.pathname.includes('/notifications') ||
        url.pathname.includes('/comments'))
    );
  },
  new NetworkFirst({
    // 치명 이슈 수정 #1: 캐시 이름에 버전 포함
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

// 3. 일반 API GET 요청 - Stale While Revalidate
registerRoute(
  ({ url, request }) => {
    return isApiRequest(url) && request.method === 'GET';
  },
  new StaleWhileRevalidate({
    // 치명 이슈 수정 #1: 캐시 이름에 버전 포함
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

// 4. API Mutation 요청 - Background Sync로 오프라인 지원
const bgSyncPlugin = new BackgroundSyncPlugin('api-mutations', {
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

registerRoute(
  ({ url, request }) => {
    return (
      isApiRequest(url) &&
      ['POST', 'PUT', 'PATCH', 'DELETE'].includes(request.method)
    );
  },
  new NetworkOnly({
    plugins: [bgSyncPlugin],
  })
);

// ===== Navigation Route (SPA) =====
const navigationRoute = new NavigationRoute(
  new NetworkFirst({
    // 치명 이슈 수정 #1: 캐시 이름에 버전 포함
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
