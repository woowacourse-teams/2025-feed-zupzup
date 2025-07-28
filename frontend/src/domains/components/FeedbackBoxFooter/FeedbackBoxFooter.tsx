import ClockIcon from '@/components/icons/clockIcon';
import LikeButton from '@/domains/components/LikeButton/LikeButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { calendar, container, content, day } from './FeedbackBoxFooter.styles';

interface FeedbackBoxFooterProps {
  likeCount: number;
  isLiked?: boolean;
  createdAt?: string;
  isSecret?: boolean;
  feedbackId: number;
}

export default function FeedbackBoxFooter({
  likeCount,
  isLiked,
  createdAt,
  isSecret,
  feedbackId,
}: FeedbackBoxFooterProps) {
  console.log('props 오류 제거용', createdAt);
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        <ClockIcon />
        <p css={day(theme)}>2분전</p>
        {/* <p css={day}>{formatDate(createdAt)}</p> */}
      </div>
      {!isSecret && (
        <div css={content(theme)}>
          <LikeButton
            like={isLiked}
            feedbackId={feedbackId}
            likeCount={likeCount}
          />
        </div>
      )}
    </div>
  );
}
