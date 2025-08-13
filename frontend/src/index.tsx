import { ThemeProvider } from '@emotion/react';
import { createRoot } from 'react-dom/client';
import './index.css';
import './reset.css';
import { theme } from './theme';
import { RouterProvider } from 'react-router-dom';
import { router } from '@/router';
import { ErrorModalProvider } from '@/contexts/useErrorModal';
import * as Sentry from '@sentry/react';

declare global {
  interface Window {
    Sentry: typeof Sentry;
  }
}

Sentry.init({
  dsn: 'https://d078b25843b4ef88ed75b287a7ab8a4b@o4509750841245696.ingest.us.sentry.io/4509750843342849',
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.replayIntegration({
      maskAllText: false,
      maskAllInputs: true,
    }),
  ],
  tracesSampleRate: 0.2,
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0,
  release: process.env.REACT_APP_VERSION || '1.0.0',
  environment: process.env.NODE_ENV || 'development',
  beforeSend(event) {
    if (process.env.NODE_ENV === 'development') {
      console.log('개발환경 에러:', event);
      return null;
    }
    return event;
  },
  sendDefaultPii: true,
});

const root = createRoot(document.getElementById('root')!);
root.render(
  <Sentry.ErrorBoundary fallback={<div>에러</div>}>
    <ThemeProvider theme={theme}>
      <ErrorModalProvider>
        <RouterProvider router={router} />
      </ErrorModalProvider>
    </ThemeProvider>
  </Sentry.ErrorBoundary>
);

if ('serviceWorker' in navigator) {
  (async () => {
    try {
      const existing = await navigator.serviceWorker.getRegistration();
      if (existing) {
        console.log('SW 재사용:', existing.scope);
      } else {
        const reg = await navigator.serviceWorker.register(
          '/firebase-messaging-sw.js'
        );
        console.log('SW 등록:', reg.scope);
      }
    } catch (err) {
      console.error('SW 등록 실패:', err);
    }
  })();
}

if (process.env.NODE_ENV === 'development') {
  (async () => {
    const { worker } = await import('./mocks/browser');
    await worker.start({
      onUnhandledRequest: 'bypass',
      serviceWorker: { url: '/firebase-messaging-sw.js' },
    });
  })();
}
