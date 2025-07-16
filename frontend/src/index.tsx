import './reset.css';
import './index.css';
import { createRoot } from 'react-dom/client';
import App from './App';
import { ThemeProvider } from '@emotion/react';
import { theme } from './theme';

const root = createRoot(document.getElementById('root')!);
root.render(
  <ThemeProvider theme={theme}>
    <App />
  </ThemeProvider>
);
