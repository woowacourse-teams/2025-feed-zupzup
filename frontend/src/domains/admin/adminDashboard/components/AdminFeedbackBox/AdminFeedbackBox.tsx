import IconButton from '@/components/IconButton/IconButton';
import CheckIcon from '@/components/icons/CheckIcon';
import LockIcon from '@/components/icons/LockIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { iconWrap, textWrap, topContainer } from './AdminFeedbackBox.styles';
import { FeedbackType } from '@/types/feedback.types';

interface AdminFeedbackBox extends Omit<FeedbackType, 'status' | 'imageUrl'> {
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
  likeCount,
  userName,
  createdAt,
}: AdminFeedbackBox) {
  return (
    <FeedbackBoxBackGround type={type}>
      <div css={topContainer}>
        <FeedbackBoxHeader
          userName={userName}
          type={type}
          feedbackId={feedbackId}
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
        <FeedbackText type={type} text={content} />
        {isSecret && (
          <p>
            <LockIcon />
          </p>
        )}
      </div>
      <FeedbackBoxFooter
        type={type}
        likeCount={likeCount}
        createdAt={createdAt}
        feedbackId={feedbackId}
      />
    </FeedbackBoxBackGround>
  );
}
