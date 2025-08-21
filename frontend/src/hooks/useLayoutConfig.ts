import { useLocation } from 'react-router-dom';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';

export const useLayoutConfig = () => {
  const location = useLocation();
  const pathname = location.pathname;

  const getLayoutConfig = (pathname: string) => {
    const exactMatch = LAYOUT_CONFIGS[pathname];

    if (exactMatch) return exactMatch;

    for (const [pattern, config] of Object.entries(LAYOUT_CONFIGS)) {
      if (matchPattern(pathname, pattern)) {
        return config;
      }
    }

    return {
      header: {
        show: false,
      },
      bottomNav: {
        show: false,
      },
    };
  };

  const matchPattern = (pathname: string, pattern: string): boolean => {
    const regex = pattern.replace(/:[\w]+/g, '[^/]+');
    const regexPattern = new RegExp(`^${regex}`);
    return regexPattern.test(pathname);
  };

  const layoutConfig = getLayoutConfig(pathname);

  return {
    isShowHeader: layoutConfig?.header?.show ?? false,
    isShowBottomNav: layoutConfig?.bottomNav?.show ?? false,
    layoutConfig,
  };
};
