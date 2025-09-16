import { Outlet } from 'react-router-dom';
import { usePageTracking } from './hooks/usePageTracking';
import { useLayoutConfig } from './hooks/useLayoutConfig';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import Header from './components/Header/Header';
import BottomNavigation from './components/BottomNavigation/BottomNavigation';
import { appContainer, main } from './App.style';
import { ModalProvider } from './contexts/useModal';
import { useToast } from '@/contexts/useToast';
import Toast from './domains/components/Toast/Toast';

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
  const { toastData, hideToast } = useToast();
  const { isShowHeader, isShowBottomNav } = useLayoutConfig();

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
        {toastData && (
          <Toast
            message={toastData.message}
            onClose={hideToast}
            duration={toastData.duration}
          />
        )}
      </div>
    </ModalProvider>
  );
}

export default function App() {
  return <AppContent />;
}
