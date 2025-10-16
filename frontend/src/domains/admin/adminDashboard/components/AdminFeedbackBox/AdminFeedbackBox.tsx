import IconButton from '@/components/IconButton/IconButton';
import CheckIcon from '@/components/icons/CheckIcon';
import LockIcon from '@/components/icons/LockIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import FeedbackAnswer from '@/domains/components/FeedbackAnswer/FeedbackAnswer';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import { FeedbackType } from '@/types/feedback.types';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { iconWrap, textWrap, topContainer } from './AdminFeedbackBox.styles';
import { memo } from 'react';
import FeedbackContent from '@/domains/components/FeedbackText/FeedbackContent';

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
  return (
    <FeedbackBoxBackGround type={type}>
      <div css={topContainer}>
        <FeedbackBoxHeader
          userName={userName}
          type={type}
          feedbackId={feedbackId}
          category={category}
        />
        <div css={iconWrap}>
          {type === 'WAITING' && (
            <IconButton
              icon={<CheckIcon />}
              onClick={() => onConfirm(feedbackId)}
            />
          )}
          <IconButton
            icon={<TrashCanIcon />}
            onClick={() => onDelete(feedbackId)}
          />
        </div>
      </div>
      <div css={textWrap}>
        <FeedbackContent type={type} text={content} imgUrl={imgUrl} />
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
