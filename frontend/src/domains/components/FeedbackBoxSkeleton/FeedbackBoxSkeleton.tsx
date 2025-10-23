import { useAppTheme } from '@/hooks/useAppTheme';
import {
  container,
  topContainer,
  iconWrap,
  textWrap,
  skeletonButton,
  skeletonAvatar,
  skeletonText,
  skeletonContent,
  skeletonFooter,
} from '@/domains/components/FeedbackBoxSkeleton/FeedbackBoxSkeleton.styles';

export default function FeedbackBoxSkeleton() {
  const theme = useAppTheme();

  return (
    <div css={container(theme)}>
      <div css={topContainer}>
        <div css={iconWrap}>
          <div css={skeletonAvatar(theme)} />
          <div css={skeletonText(theme, 'short')} />
        </div>

        <div css={iconWrap}>
          <div css={skeletonButton(theme)} />
          <div css={skeletonButton(theme)} />
        </div>
      </div>

      <div css={textWrap}>
        <div css={skeletonContent(theme)} />
      </div>

      <div css={skeletonFooter}>
        <div css={skeletonText(theme, 'medium')} />
        <div css={skeletonText(theme, 'short')} />
      </div>
    </div>
  );
}
