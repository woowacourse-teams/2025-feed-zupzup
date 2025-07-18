import CategoryTag from '../../../../components/CategoryTag/CategoryTag';
import StatusTag from '../../../../components/StatusTag/StatusTag';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { tagContainer } from './UserFeedbackBox.styles';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

interface UserFeedbackBox {
  type: FeedbackStatusType;
}

export default function UserFeedbackBox({ type }: UserFeedbackBox) {
  return (
    <FeedbackBoxBackGround type={type}>
      <div css={tagContainer}>
        <CategoryTag text='맛' type={type} />
        <StatusTag type={type} />
      </div>
      <FeedbackText
        type={type}
        text='평소에 나트륨을 적게 먹으려고 노력하는데, 오늘
먹은 음식이 너무 짰어요. 조금 더 담백하게 해주시면 좋겠습니다.'
      />
      <FeedbackBoxFooter />
    </FeedbackBoxBackGround>
  );
}
