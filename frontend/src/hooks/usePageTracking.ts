import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

declare global {
  interface Window {
    gtag: (
      command: string,
      action: string,
      params?: {
        page_path?: string;
        page_title?: string;
      }
    ) => void;
  }
}

const GA_MEASUREMENT_ID = process.env.GA_ID || 'G-';

export const usePageTracking = () => {
  const location = useLocation();
  useEffect(() => {
    window.gtag('config', GA_MEASUREMENT_ID, {
      page_path: location.pathname + location.search,
      page_title: document.title,
    });
  }, [location]);
};
