import { ErrorProvider } from '@/contexts/useErrorContext';
import { ToastProvider } from '@/contexts/useToast';
import { router } from '@/router';
import { initSentry } from '@/services/sentry';
import { registerServiceWorker } from '@/services/serviceWorker';
import { setupGlobalVersion } from '@/utils/version';
import { ThemeProvider } from '@emotion/react';
import * as Sentry from '@sentry/react';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import './index.css';
import './reset.css';
import { theme } from './theme';
import QueryClientBoundary from '@/error/QueryClientBoundary/QueryClientBoundary';
//import { setupMockServiceWorker } from './services/msw';

declare global {
  interface Window {
    Sentry: typeof Sentry;
    __APP_VERSION__: {
      version: string;
      buildHash: string;
      buildHashFull: string;
      branch: string;
      buildTime: string;
      commitMessage: string;
    };
    getVersion: () => void;
  }
}

setupGlobalVersion();
initSentry();
registerServiceWorker();
//setupMockServiceWorker();

const root = createRoot(document.getElementById('root')!);
root.render(
  <ErrorProvider>
    <QueryClientBoundary>
      <ThemeProvider theme={theme}>
        <ToastProvider>
          <Sentry.ErrorBoundary>
            <RouterProvider router={router} />
            {process.env.NODE_ENV === 'development' && (
              <ReactQueryDevtools initialIsOpen={false} />
            )}
          </Sentry.ErrorBoundary>
        </ToastProvider>
      </ThemeProvider>
    </QueryClientBoundary>
  </ErrorProvider>
);
