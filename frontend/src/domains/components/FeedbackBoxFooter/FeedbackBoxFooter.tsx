import { useAppTheme } from '@/hooks/useAppTheme';
import CalendarIcon from '@/components/icons/CalendarIcon';
import LikeButton from '@/components/LikeButton/LikeButton';
import { calendar, container, content, day } from './FeedbackBoxFooter.styles';
import formatDate from '@/utils/formatDate';

interface FeedbackBoxFooterProps {
  likeCount?: number;
  isLiked?: boolean;
  createdAt: string;
  userName?: string;
  isSecret?: boolean;
  feedbackId: number;
}

export default function FeedbackBoxFooter({
  likeCount,
  isLiked,
  createdAt,
  userName,
  isSecret,
  feedbackId,
}: FeedbackBoxFooterProps) {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        {userName}
        <p>
          <CalendarIcon />
        </p>
        <p css={day}>{formatDate(createdAt)}</p>
      </div>
      {!isSecret && (
        <div css={content(theme)}>
          <LikeButton like={isLiked} feedbackId={feedbackId} /> {likeCount}
        </div>
      )}
    </div>
  );
}
