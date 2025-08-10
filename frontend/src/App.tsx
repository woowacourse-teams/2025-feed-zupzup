import { Outlet } from 'react-router-dom';
import { usePageTracking } from './hooks/usePageTracking';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import Header from './components/Header/Header';
import BottomNavigation from './components/BottomNavigation/BottomNavigation';
import { useLocation } from 'react-router-dom';
import { appContainerStyle } from './App.style';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';

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

export default function App() {
  usePageTracking();
  const { isError, setErrorFalse, message, title } = useErrorModalContext();
  const location = useLocation();

  const hasHeader =
    LAYOUT_CONFIGS[location.pathname] !== undefined &&
    LAYOUT_CONFIGS[location.pathname].header.show;

  return (
    <div css={appContainerStyle(hasHeader)}>
      <Header />
      <main>
        <Outlet />
      </main>
      <BottomNavigation />
      {isError && (
        <AlertModal
          onClose={setErrorFalse}
          isOpen={isError}
          title={title}
          message={message}
        />
      )}
    </div>
  );
}
