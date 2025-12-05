import LockIcon from '@/components/icons/LockIcon';
import FeedbackAnswer from '@/domains/components/FeedbackAnswer/FeedbackAnswer';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import { FeedbackType } from '@/types/feedback.types';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import {
  checkButton,
  deleteButton,
  iconWrap,
  textWrap,
  topContainer,
} from './AdminFeedbackBox.styles';
import { memo } from 'react';
import FeedbackContent from '@/domains/components/FeedbackText/FeedbackContent';
import { useAppTheme } from '@/hooks/useAppTheme';

interface AdminFeedbackBox extends Omit<FeedbackType, 'status' | 'imageUrl'> {
  type: FeedbackStatusType;
  feedbackId: number;
  onConfirm: (feedbackId: number) => void;
  onDelete: (feedbackId: number) => void;
  imgUrl: string | null;
}

export default memo(function AdminFeedbackBox({
  type,
  feedbackId,
  onConfirm,
  onDelete,
  content,
  isSecret,
  likeCount,
  userName,
  comment,
  postedAt,
  category,
  imgUrl,
}: AdminFeedbackBox) {
  const theme = useAppTheme();

  return (
    <FeedbackBoxBackGround type={type}>
      <div css={topContainer}>
        <FeedbackBoxHeader
          userName={userName}
          type={type}
          feedbackId={feedbackId}
          category={category}
        />
        <div css={iconWrap(theme)}>
          {type === 'WAITING' && (
            <button
              css={checkButton(theme)}
              onClick={() => onConfirm(feedbackId)}
            >
              완료
            </button>
          )}
          <button
            css={deleteButton(theme)}
            onClick={() => onDelete(feedbackId)}
          >
            삭제
          </button>
        </div>
      </div>
      <div css={textWrap}>
        <FeedbackContent text={content} imgUrl={imgUrl} />
        {isSecret && (
          <p>
            <LockIcon />
          </p>
        )}
      </div>
      {type === 'CONFIRMED' && comment && <FeedbackAnswer answer={comment} />}
      <FeedbackBoxFooter
        type={type}
        isAdmin={true}
        likeCount={likeCount}
        postedAt={postedAt}
        feedbackId={feedbackId}
      />
    </FeedbackBoxBackGround>
  );
});
