import { useAppTheme } from '@/hooks/useAppTheme';
import Calendar from '@/components/icons/Calendar';
import LikeButton from '@/components/LikeButton/LikeButton';
import { calendar, container, content, day } from './FeedbackBoxFooter.styles';

interface FeedbackBoxFooterProps {
  likeCount?: number;
}

export default function FeedbackBoxFooter({
  likeCount,
}: FeedbackBoxFooterProps) {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        <p>
          <Calendar />
        </p>
        <p css={day}>2025-01-08</p>
      </div>
      <div css={content(theme)}>
        <LikeButton like={false} /> {likeCount}
      </div>
    </div>
  );
}
