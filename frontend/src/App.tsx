import { Outlet } from 'react-router-dom';
import { HelmetProvider } from 'react-helmet-async';
import { usePageTracking } from './hooks/usePageTracking';
import { useLayoutConfig } from './hooks/useLayoutConfig';
import Header from './components/Header/Header';
import BottomNavigation from './components/BottomNavigation/BottomNavigation';
import { appContainer, main } from './App.style';
import ModalProvider from './stores/Modal/ModalProvider';
import ToastProvider from './stores/Toast/ToastProvider';

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
  const { isShowHeader, isShowBottomNav } = useLayoutConfig();

  return (
    <div>
      <ModalProvider />
      <ToastProvider />
      <div css={appContainer(isShowHeader)}>
        {isShowHeader && <Header />}
        <main css={main}>
          <Outlet />
        </main>
        {isShowBottomNav && <BottomNavigation />}
      </div>
    </div>
  );
}

export default function App() {
  return (
    <HelmetProvider>
      <AppContent />
    </HelmetProvider>
  );
}
