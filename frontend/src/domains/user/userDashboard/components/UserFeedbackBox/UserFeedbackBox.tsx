import LockIcon from '@/components/icons/LockIcon';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { secretText } from './UserFeedbackBox.styles';
import { SerializedStyles } from '@emotion/react';

interface UserFeedbackBox {
  userName: string;
  type: FeedbackStatusType;
  content: string;
  isLiked: boolean;
  isSecret: boolean;
  createdAt: string;
  feedbackId: number;
  likeCount: number;
  customCSS: SerializedStyles | null;
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
  customCSS,
}: UserFeedbackBox) {
  const theme = useAppTheme();

  return (
    <FeedbackBoxBackGround type={type} customCSS={customCSS}>
      <FeedbackBoxHeader
        userName={userName}
        type={type}
        feedbackId={feedbackId}
      />
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
