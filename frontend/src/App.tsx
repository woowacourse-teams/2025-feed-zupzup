import { useEffect } from 'react';
import { syncVisitorId } from '@/utils/visitorId';
import { Outlet } from 'react-router-dom';
import { HelmetProvider } from 'react-helmet-async';
import { usePageTracking } from './hooks/usePageTracking';
import { useLayoutConfig } from './hooks/useLayoutConfig';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import Header from './components/Header/Header';
import BottomNavigation from './components/BottomNavigation/BottomNavigation';
import { appContainer, main } from './App.style';
import { ModalProvider } from './contexts/useModal';

const gaId = process.env.GA_ID;

if (gaId) {
  const script1 = document.createElement('script');
  script1.async = true;
  script1.src = `https://www.googletagmanager.com/gtag/js?id=${gaId}`;
  document.head.appendChild(script1);

  const script2 = document.createElement('script');
  script2.innerHTML = `
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());
    gtag('config', '${gaId}');
  `;
  document.head.appendChild(script2);
}

function AppContent() {
  usePageTracking();
  const { isError, setErrorFalse, message, title } = useErrorModalContext();
  const { isShowHeader, isShowBottomNav } = useLayoutConfig();
  // TODO: 백엔드에서 쿠키 관련 플래그 수정해야 테스트 해볼수있음.
  // TODO: 좋아요 로직, 피드백 작성 API로직에 인터셉트 추가해야함.

  useEffect(() => {
    syncVisitorId();
  }, []);

  return (
    <ModalProvider>
      <div css={appContainer(isShowHeader)}>
        {isShowHeader && <Header />}
        <main css={main}>
          <Outlet />
        </main>
        {isShowBottomNav && <BottomNavigation />}
        {isError && (
          <AlertModal
            onClose={setErrorFalse}
            isOpen={isError}
            title={title}
            message={message}
          />
        )}
      </div>
    </ModalProvider>
  );
}

export default function App() {
  return (
    <HelmetProvider>
      <AppContent />
    </HelmetProvider>
  );
}
