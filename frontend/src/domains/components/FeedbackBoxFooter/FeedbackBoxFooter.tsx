import ClockIcon from '@/components/icons/CClockIcon';
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
          {type === 'WAITING' ? (
            <Tag>👀 검토중</Tag>
          ) : (
            <Tag customCSS={confirmedText(theme)}> ✓ 반영됨</Tag>
          )}
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
