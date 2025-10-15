import { useOffline } from '@/hooks/useOffline';
import { useAppTheme } from '@/hooks/useAppTheme';
import { offlineBannerStyle } from './OfflineBanner.styles';

export const OfflineBanner = () => {
  const isOffline = useOffline();
  const theme = useAppTheme();

  if (!isOffline) return null;

  return (
    <div css={offlineBannerStyle(theme)}>
      네트워크 상태가 원활하지 않습니다.
    </div>
  );
};
