import { deleteFeedback, patchFeedbackStatus } from '@/apis/adminFeedback.api';
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

interface AdminFeedbackBox
  extends Omit<AdminFeedback, 'feedbackId' | 'status'> {
  type: FeedbackStatusType;
}

const FEEDBACK_ID = 28; // TODO : 삭제 필요. 임시 feedbackId

export default function AdminFeedbackBox({
  type,
  content,
  isSecret,
  imgUrl,
  likeCount,
  userName,
  createdAt,
}: AdminFeedbackBox) {
  const handleDelete = (feedbackId: number) => {
    deleteFeedback({ feedbackId });
    alert('피드백이 삭제되었습니다.');
  };

  const handleStatusChange = (feedbackId: number, status: string) => {
    patchFeedbackStatus({
      feedbackId,
      status,
    });
    alert('피드백 상태가 변경되었습니다.');
  };

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
              onClick={() => handleStatusChange(FEEDBACK_ID, 'CONFIRMED')}
            />
          )}
          <IconButton
            icon={<TrashCanIcon />}
            onClick={() => handleDelete(FEEDBACK_ID)}
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
