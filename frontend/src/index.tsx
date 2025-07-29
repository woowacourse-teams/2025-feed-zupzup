import { ThemeProvider } from '@emotion/react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import './index.css';
import './reset.css';
import { theme } from './theme';
import { router } from './router';
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: 'https://d078b25843b4ef88ed75b287a7ab8a4b@o4509750841245696.ingest.us.sentry.io/4509750843342849',
  integrations: [Sentry.browserTracingIntegration()],
  tracesSampleRate: 0.2,
  sendDefaultPii: true,
});

const root = createRoot(document.getElementById('root')!);
root.render(
  <ThemeProvider theme={theme}>
    <RouterProvider router={router} />
  </ThemeProvider>
);
