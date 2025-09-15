import {
  container,
  contentContainer,
  titleBadge,
  titleText,
  subtitleText,
  iconContainer,
} from './OnBoardingSlide.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface OnboardingSlideProps {
  title: string;
  subtitle: string | React.ReactNode;
  icon: React.ReactNode;
  isActive: boolean;
}

export function OnboardingSlide({
  title,
  subtitle,
  icon,
}: OnboardingSlideProps) {
  const theme = useAppTheme();

  return (
    <div css={container(theme)}>
      <div css={contentContainer}>
        <div css={titleBadge(theme)}>
          <span css={titleText(theme)}>{title}</span>
        </div>
        <h1 css={subtitleText(theme)}>{subtitle}</h1>
      </div>

      <div css={iconContainer}>{icon}</div>
    </div>
  );
}
