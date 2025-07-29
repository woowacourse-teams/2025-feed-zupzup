import { ThemeProvider } from '@emotion/react';
import { createRoot } from 'react-dom/client';
import './index.css';
import './reset.css';
import { theme } from './theme';
import { RouterProvider } from 'react-router-dom';
import { router } from '@/router';

const root = createRoot(document.getElementById('root')!);
root.render(
  <ThemeProvider theme={theme}>
    <RouterProvider router={router} />
  </ThemeProvider>
);
