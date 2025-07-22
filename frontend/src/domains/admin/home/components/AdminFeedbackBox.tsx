import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import CheckIcon from '@/components/icons/CheckIcon';
import CategoryTag from '@/domains/components/CategoryTag/CategoryTag';
import StatusTag from '@/domains/components/StatusTag/StatusTag';
import { iconWrap, topContainer } from './AdminFeedbackBox.styles';
import FeedbackImage from '@/domains/components/FeedbackImage/FeedbackImage';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import IconButton from '@/components/IconButton/IconButton';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import { AdminFeedback } from '@/types/feedback.types';

interface AdminFeedbackBox
  extends Omit<AdminFeedback, 'feedbackId' | 'status'> {
  type: FeedbackStatusType;
}

export default function AdminFeedbackBox({
  type,
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
          {type === 'WAITING' && <IconButton icon={<CheckIcon />} />}
          <IconButton icon={<TrashCanIcon />} />
        </div>
      </div>
      {isSecret ? '비밀글입니다.' : <FeedbackText type={type} text={content} />}
      {imgUrl && <FeedbackImage src={imgUrl} />}
      <FeedbackBoxFooter
        likeCount={likeCount}
        createdAt={createdAt}
        userName={userName}
      />
    </FeedbackBoxBackGround>
  );
}
