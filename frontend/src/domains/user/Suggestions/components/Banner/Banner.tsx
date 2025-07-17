import { useAppTheme } from '@/hooks/useAppTheme';
import { banner, bannerDescription } from './Banner.style';
import { BannerProps } from './Banner.type';

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
