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
  createdAt?: string;
  isSecret?: boolean;
  feedbackId: number;
  type: FeedbackStatusType;
}

export default function FeedbackBoxFooter({
  likeCount,
  isLiked,
  createdAt,
  isSecret,
  feedbackId,
  type,
}: FeedbackBoxFooterProps) {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={calendar(theme)}>
        <ClockIcon />
        <p css={day(theme)}>{formatRelativeTime(createdAt ?? '')}</p>
      </div>

      <div css={content(theme)}>
        {type === 'CONFIRMED' && (
          <Tag customCSS={confirmedText(theme)}> ✅ 반영됨</Tag>
        )}
        {!isSecret && (
          <LikeButton
            like={isLiked}
            feedbackId={feedbackId}
            likeCount={likeCount}
          />
        )}
      </div>
    </div>
  );
}
