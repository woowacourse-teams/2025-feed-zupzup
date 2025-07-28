import IconButton from '@/components/IconButton/IconButton';
import CheckIcon from '@/components/icons/CheckIcon';
import LockIcon from '@/components/icons/LockIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import CategoryTag from '@/domains/components/CategoryTag/CategoryTag';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackImage from '@/domains/components/FeedbackImage/FeedbackImage';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import StatusTag from '@/domains/components/StatusTag/StatusTag';
import { AdminFeedback } from '@/types/feedback.types';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { iconWrap, textWrap, topContainer } from './AdminFeedbackBox.styles';

interface AdminFeedbackBox extends Omit<AdminFeedback, 'status'> {
  type: FeedbackStatusType;
  feedbackId: number;
  onConfirm: (feedbackId: number) => void;
  onDelete: (feedbackId: number) => void;
}

export default function AdminFeedbackBox({
  type,
  feedbackId,
  onConfirm,
  onDelete,
  content,
  isSecret,
  imageUrl,
  likeCount,
  userName,
  createdAt,
}: AdminFeedbackBox) {
  console.log(userName);
  return (
    <FeedbackBoxBackGround type={type}>
      <div css={topContainer}>
        <div css={iconWrap}>
          <CategoryTag text='맛' type={type} />
          <StatusTag type={type} />
        </div>
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
        <FeedbackText type={type} text={content} />
        {isSecret && (
          <p>
            <LockIcon />
          </p>
        )}
      </div>
      {imageUrl && <FeedbackImage src={imageUrl} />}
      <FeedbackBoxFooter
        likeCount={likeCount}
        createdAt={createdAt}
        feedbackId={feedbackId}
      />
    </FeedbackBoxBackGround>
  );
}
