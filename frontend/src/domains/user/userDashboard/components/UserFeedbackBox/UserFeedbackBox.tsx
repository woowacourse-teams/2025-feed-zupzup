import LockIcon from '@/components/icons/LockIcon';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { secretText } from './UserFeedbackBox.styles';

interface UserFeedbackBox {
  userName: string;
  type: FeedbackStatusType;
  content: string;
  isLiked: boolean;
  isSecret: boolean;
  createdAt: string;
  feedbackId: number;
  likeCount: number;
}

export default function UserFeedbackBox({
  userName,
  type,
  content,
  isLiked,
  isSecret,
  createdAt,
  feedbackId,
  likeCount,
}: UserFeedbackBox) {
  const theme = useAppTheme();

  return (
    <FeedbackBoxBackGround type={type}>
      <FeedbackBoxHeader userName={userName} type={type} />
      {isSecret ? (
        <div css={secretText(theme)}>
          <p>비밀글입니다.</p>
          <p>
            <LockIcon />
          </p>
        </div>
      ) : (
        <FeedbackText type={type} text={content} />
      )}
      <FeedbackBoxFooter
        type={type}
        isLiked={isLiked}
        createdAt={createdAt}
        isSecret={isSecret}
        feedbackId={feedbackId}
        likeCount={likeCount}
      />
    </FeedbackBoxBackGround>
  );
}
