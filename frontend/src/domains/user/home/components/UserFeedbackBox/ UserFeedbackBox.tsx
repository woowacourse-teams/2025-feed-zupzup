import { css } from '@emotion/react';
import CategoryTag from '../CategoryTag/CategoryTag';
import StatusTag from '../StatusTag/StatusTag';
import FeedbackText from '@/components/FeedbackText/FeedbackText';
import FeedbackBoxFooter from '@/components/FeedbackBoxFooter/FeedbackBoxFooter';

export default function UserFeedbackBox() {
  return (
    <div css={container}>
      <div css={tagContainer}>
        <CategoryTag text='맛' type='incomplete' />
        <StatusTag type='incomplete'>접수</StatusTag>
      </div>
      <FeedbackText
        text='평소에 나트륨을 적게 먹으려고 노력하는데, 오늘
먹은 음식이 너무 짰어요. 조금 더 담백하게 해주시면 좋겠습니다.'
      />
      <FeedbackBoxFooter />
    </div>
  );
}

const container = css`
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  min-height: 100px;
  padding: 18px;
  border-radius: 14px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);
`;

const tagContainer = css`
  display: flex;
  gap: 14px;
`;
