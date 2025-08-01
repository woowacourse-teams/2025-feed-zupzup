import { ThemeProvider } from '@emotion/react';
import { createRoot } from 'react-dom/client';
import './index.css';
import './reset.css';
import { theme } from './theme';
import { RouterProvider } from 'react-router-dom';
import { router } from '@/router';
import { ErrorModalProvider } from '@/contexts/useErrorModal';

if (process.env.NODE_ENV === 'development') {
  const { worker } = await import('./mocks/browser');
  await worker.start({
    onUnhandledRequest: 'bypass',
  });
}

const root = createRoot(document.getElementById('root')!);
root.render(
  <ErrorModalProvider>
    <ThemeProvider theme={theme}>
      <RouterProvider router={router} />
    </ThemeProvider>
  </ErrorModalProvider>
);
