import { useLocation } from 'react-router-dom';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';

export const useLayoutConfig = () => {
  const location = useLocation();

  const layoutConfig =
    LAYOUT_CONFIGS[location.pathname as keyof typeof LAYOUT_CONFIGS];

  return {
    isShowHeader: layoutConfig?.header?.show ?? false,
    isShowBottomNav: layoutConfig?.bottomNav?.show ?? false,
    layoutConfig,
  };
};
