import { ThemeProvider } from '@emotion/react';
import { createRoot } from 'react-dom/client';
import './index.css';
import './reset.css';
import { theme } from './theme';
import { RouterProvider } from 'react-router-dom';
import { router } from '@/router';
import * as Sentry from '@sentry/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { setupGlobalVersion } from '@/utils/version';
import { initSentry } from '@/services/sentry';
import { registerServiceWorker } from '@/services/serviceWorker';
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

const queryClient = new QueryClient({
  defaultOptions: {
    mutations: {
      networkMode: 'always',
    },
    queries: {
      networkMode: 'always',
      throwOnError: true,
    },
  },
});

const root = createRoot(document.getElementById('root')!);
root.render(
  <QueryClientProvider client={queryClient}>
    <ThemeProvider theme={theme}>
      <Sentry.ErrorBoundary>
        <RouterProvider router={router} />
        {process.env.NODE_ENV === 'development' && (
          <ReactQueryDevtools initialIsOpen={false} />
        )}
      </Sentry.ErrorBoundary>
    </ThemeProvider>
  </QueryClientProvider>
);
