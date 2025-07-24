import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import CheckIcon from '@/components/icons/CheckIcon';
import CategoryTag from '@/domains/components/CategoryTag/CategoryTag';
import StatusTag from '@/domains/components/StatusTag/StatusTag';
import { iconWrap, textWrap, topContainer } from './AdminFeedbackBox.styles';
import FeedbackImage from '@/domains/components/FeedbackImage/FeedbackImage';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import IconButton from '@/components/IconButton/IconButton';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import { AdminFeedback } from '@/types/feedback.types';
import LockIcon from '@/components/icons/LockIcon';

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
  imgUrl,
  likeCount,
  userName,
  createdAt,
}: AdminFeedbackBox) {
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
      {imgUrl && <FeedbackImage src={imgUrl} />}
      <FeedbackBoxFooter
        likeCount={likeCount}
        createdAt={createdAt}
        userName={userName}
      />
    </FeedbackBoxBackGround>
  );
}
