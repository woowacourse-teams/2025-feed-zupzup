import CategoryTag from '../../../../components/CategoryTag/CategoryTag';
import StatusTag from '../../../../components/StatusTag/StatusTag';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { tagContainer } from './UserFeedbackBox.styles';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

interface UserFeedbackBox {
  type: FeedbackStatusType;
  content: string;
  isLiked: boolean;
  isSecret: boolean;
  createdAt: string;
}

export default function UserFeedbackBox({
  type,
  content,
  isLiked,
  isSecret,
  createdAt,
}: UserFeedbackBox) {
  return (
    <FeedbackBoxBackGround type={type}>
      <div css={tagContainer}>
        <CategoryTag text='맛' type={type} />
        <StatusTag type={type} />
      </div>
      {isSecret ? '비밀글입니다.' : <FeedbackText type={type} text={content} />}
      <FeedbackBoxFooter isLiked={isLiked} createdAt={createdAt} />
    </FeedbackBoxBackGround>
  );
}
