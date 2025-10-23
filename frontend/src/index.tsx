import { ErrorProvider } from '@/contexts/useErrorContext';
import { ToastProvider } from '@/contexts/useToast';
import { PWAPromptProvider, usePWAPrompt } from '@/contexts/usePWAPrompt';
import { router } from '@/router';
import { initSentry } from '@/services/sentry';
import { registerServiceWorker } from '@/services/serviceWorker';
import { setupGlobalVersion } from '@/utils/version';
import { ThemeProvider } from '@emotion/react';
import * as Sentry from '@sentry/react';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import PWAPrompt from 'react-ios-pwa-prompt';
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

function PWAPromptWrapper() {
  const { isShown, hidePrompt } = usePWAPrompt();

  return (
    <PWAPrompt
      timesToShow={999}
      delay={500}
      isShown={isShown}
      onClose={hidePrompt}
      appIconPath='/512x512.png'
      copyTitle='피드줍줍 앱 설치'
      copySubtitle='더 빠르고 편리하게 사용하세요'
      copyDescription='홈 화면에 추가하면 앱처럼 사용할 수 있어요. 빠른 접근과 푸시 알림을 받아보세요. 크롬은 주소창, 사파리는 주소창이 있는 쪽의 탭을 확인해주세요.'
      copyShareStep='1) 아래 공유 버튼 선택. 안보인다면 ••• 버튼 선택'
      copyAddToHomeScreenStep='2) 홈 화면에 추가 선택'
    />
  );
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
        <PWAPromptProvider>
          <ToastProvider>
            <Sentry.ErrorBoundary>
              <RouterProvider router={router} />
              <PWAPromptWrapper />
              {process.env.NODE_ENV === 'development' && (
                <ReactQueryDevtools initialIsOpen={false} />
              )}
            </Sentry.ErrorBoundary>
          </ToastProvider>
        </PWAPromptProvider>
      </ThemeProvider>
    </QueryClientBoundary>
  </ErrorProvider>
);
