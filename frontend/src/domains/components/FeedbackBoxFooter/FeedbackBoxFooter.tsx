import { useAppTheme } from '@/hooks/useAppTheme';
import CalendarIcon from '@/components/icons/CalendarIcon';
import LikeButton from '@/components/LikeButton/LikeButton';
import { calendar, container, content, day } from './FeedbackBoxFooter.styles';

interface FeedbackBoxFooterProps {
  likeCount?: number;
  isLiked: boolean;
  createdAt: string;
}

export default function FeedbackBoxFooter({
  likeCount,
  isLiked,
  createdAt,
}: FeedbackBoxFooterProps) {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        <p>
          <CalendarIcon />
        </p>
        <p css={day}>{createdAt}</p>
      </div>
      <div css={content(theme)}>
        <LikeButton like={isLiked} /> {likeCount}
      </div>
    </div>
  );
}
