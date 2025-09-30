import ClockIcon from '@/components/icons/ClockIcon';
import LikeButton from '@/domains/components/LikeButton/LikeButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  calendar,
  confirmedText,
  container,
  content,
  day,
} from './FeedbackBoxFooter.styles';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import Tag from '@/components/Tag/Tag';
import { formatRelativeTime } from '@/utils/formatRelativeTime';

interface FeedbackBoxFooterProps {
  likeCount: number;
  isLiked?: boolean;
  postedAt?: string;
  isSecret?: boolean;
  feedbackId: number;
  type: FeedbackStatusType;
  isAdmin?: boolean;
}

export default function FeedbackBoxFooter({
  likeCount,
  isLiked,
  postedAt,
  isSecret,
  feedbackId,
  type,
  isAdmin = false,
}: FeedbackBoxFooterProps) {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        <ClockIcon />
        <p css={day(theme)}>{formatRelativeTime(postedAt ?? '')}</p>
      </div>

      <div css={content(theme)}>
        {type === 'CONFIRMED' && (
          <Tag customCSS={confirmedText(theme)}> ✅ 완료</Tag>
        )}
        {!isSecret && (
          <LikeButton
            like={isLiked}
            feedbackId={feedbackId}
            likeCount={likeCount}
            isAdmin={isAdmin}
          />
        )}
      </div>
    </div>
  );
}
