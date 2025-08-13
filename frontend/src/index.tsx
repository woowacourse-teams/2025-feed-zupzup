import { ThemeProvider } from '@emotion/react';
import { createRoot } from 'react-dom/client';
import './index.css';
import './reset.css';
import { theme } from './theme';
import { RouterProvider } from 'react-router-dom';
import { router } from '@/router';
import { ErrorModalProvider } from '@/contexts/useErrorModal';
import * as Sentry from '@sentry/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

declare global {
  interface Window {
    Sentry: typeof Sentry;
  }
}

// if (process.env.NODE_ENV === 'development') {
//   const { worker } = await import('./mocks/browser');
//   await worker.start({
//     onUnhandledRequest: 'bypass',
//   });
// }

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker
      .register('/service-worker.js')
      .then((reg) => console.log('Service Worker registered:', reg))
      .catch((err) => console.log('Service Worker registration failed:', err));
  });
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

window.Sentry = Sentry;

const queryClient = new QueryClient();

const root = createRoot(document.getElementById('root')!);
root.render(
  <QueryClientProvider client={queryClient}>
    <ErrorModalProvider>
      <ThemeProvider theme={theme}>
        <Sentry.ErrorBoundary>
          <RouterProvider router={router} />
        </Sentry.ErrorBoundary>
      </ThemeProvider>
    </ErrorModalProvider>
  </QueryClientProvider>
);
