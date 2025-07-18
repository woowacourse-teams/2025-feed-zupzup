import CategoryTag from '../CategoryTag/CategoryTag';
import StatusTag from '../StatusTag/StatusTag';
import FeedbackText from '@/components/FeedbackText/FeedbackText';
import FeedbackBoxFooter from '@/components/FeedbackBoxFooter/FeedbackBoxFooter';
import { tagContainer, container } from './UserFeedbackBox.styles';

interface UserFeedbackBox {
  type: 'incomplete' | 'complete';
}

export default function UserFeedbackBox({ type }: UserFeedbackBox) {
  return (
    <div css={container}>
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
    </div>
  );
}
