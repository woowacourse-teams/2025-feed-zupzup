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

interface AdminFeedbackBox {
  type: FeedbackStatusType;
}

export default function AdminFeedbackBox({ type }: AdminFeedbackBox) {
  return (
    <FeedbackBoxBackGround type={type}>
      <div css={topContainer}>
        <div css={iconWrap}>
          <CategoryTag text='맛' type={type} />
          <StatusTag type={type} />
        </div>
        <div css={iconWrap}>
          {type === 'incomplete' && <IconButton icon={<CheckIcon />} />}
          <IconButton icon={<TrashCanIcon />} />
        </div>
      </div>
      <FeedbackText
        text='평소에 나트륨을 적게 먹으려고 노력하는데, 오늘
         먹은 음식이 너무 짰어요. 조금 더 담백하게 해주시면 좋겠습니다.'
        type={type}
      />
      <FeedbackImage src='https://i.pinimg.com/236x/44/f9/83/44f9831be884e4c65f167b96e16fa94e.jpg' />
      <FeedbackBoxFooter likeCount={6} />
    </FeedbackBoxBackGround>
  );
}
