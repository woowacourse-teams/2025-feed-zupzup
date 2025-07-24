import {
  banner,
  bannerDescription,
} from '@/domains/user/suggestions/components/Banner/Banner.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface BannerProps {
  title: string;
  description: string;
}

export default function Banner({ title, description }: BannerProps) {
  const theme = useAppTheme();

  return (
    <div css={banner(theme)}>
      <p css={theme.typography.inter.bodyMedium}>{title}</p>
      <p css={[bannerDescription(theme), theme.typography.inter.small]}>
        {description}
      </p>
    </div>
  );
}
