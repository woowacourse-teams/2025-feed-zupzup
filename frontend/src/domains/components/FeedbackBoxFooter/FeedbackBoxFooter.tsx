import { useAppTheme } from '@/hooks/useAppTheme';
import CalendarIcon from '@/components/icons/CalendarIcon';
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
          <CalendarIcon />
        </p>
        <p css={day}>2025-01-08</p>
      </div>
      <div css={content(theme)}>
        <LikeButton like={false} /> {likeCount}
      </div>
    </div>
  );
}
