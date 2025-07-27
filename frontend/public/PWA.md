## ✅ 1. 필수 파일 구조 (public/)

```csharp
public/
├── 192x192.png           # 앱 아이콘
├── 512x512.png           # 앱 아이콘 (PWA install용)
├── favicon.ico           # 브라우저 탭 아이콘
├── index.html            # HTML 템플릿
├── manifest.json         # PWA 메타 정보
└── service-worker.js     # Service Worker 파일
```

<br/>

## ✅ 2. manifest.json 설정

`public/manifest.json` 파일 내용 예시:

```json
{
  "short_name": "feed_zupzup",
  "name": "feed_zupzup Web App",
  "icons": [
    {
      "src": "192x192.png",
      "sizes": "192x192",
      "type": "image/png",
      "purpose": "maskable"
    },
    {
      "src": "512x512.png",
      "sizes": "512x512",
      "type": "image/png"
    }
  ],
  "start_url": "/",
  "display": "standalone",
  "theme_color": "#000000",
  "background_color": "#ffffff"
}
```

<br/>

## ✅ 3. index.html 설정

```html
<head>
  ...
  <link rel="manifest" href="/manifest.json" />
  <meta name="theme-color" content="#1976d2" />
  ...
</head>
```

> ※ theme-color는 PWA 툴바 색상 설정용 (선택 사항이나 추천).

<br/>

## ✅ 4. service-worker.js

`public/service-worker.js`

```js
/* eslint-env browser, serviceworker */

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

// 이후 앱 push 관련 로직도 여기에 작성하면 됨!!
```

<br/>

## ✅ 5. Webpack 설정

`webpack.common.js` 또는 `webpack.config.js`

```js
import CopyWebpackPlugin from 'copy-webpack-plugin';

new CopyWebpackPlugin({
  patterns: [
    { from: 'public/favicon.ico', to: '.' },
    { from: 'public/manifest.json', to: '.' },
    { from: 'public/512x512.png', to: '.' },
    { from: 'public/192x192.png', to: '.' },
    { from: 'public/service-worker.js', to: '.' },
  ],
}),

...

devServer: {
  historyApiFallback: true, // SPA 라우팅 지원 (404 방지) - PWA + React Router를 함께 쓴다면 이 옵션은 필수에 가까움
  static: './dist',         // 정적 파일 서빙 (PWA 필수 리소스 포함)
  port: 3000,               // 개발 포트 설정
}



```

<br/>

## ✅ 6. Service Worker 등록

`src/index.tsx` 파일 상단에 다음 코드 추가:

```tsx
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker
      .register('/service-worker.js')
      .then((reg) => console.log('Service Worker registered:', reg))
      .catch((err) => console.log('Service Worker registration failed:', err));
  });
}
```

<br/>

## ✅ 7. ESLint 설정 (옵션)

`eslint.config.js`

```js
{
  files: ['public/service-worker.js'],
  languageOptions: {
    parser: tseslint.parser,
    parserOptions: {
      ecmaVersion: 'latest',
      sourceType: 'script', // service worker는 script 타입
    },
  },
  env: {
    browser: true,
    serviceworker: true,
  },
}

```

<br/>

## ✅ 8. 확인 사항

- manifest.json이 잘 링크되었는지
- 512x512.png 아이콘이 제대로 존재하는지
- service-worker.js가 정상적으로 등록되는지 (콘솔 확인)
- 설치 버튼이 브라우저 주소창 오른쪽에 뜨는지 (조건 충족 시) > 보이면 성공!
