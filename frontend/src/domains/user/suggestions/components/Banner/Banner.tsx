import { banner } from '@/domains/user/suggestions/components/Banner/Banner.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface BannerProps {
  children: React.ReactNode;
}

export default function Banner({ children }: BannerProps) {
  const theme = useAppTheme();

  return <div css={banner(theme)}>{children}</div>;
}
