import LockIcon from '@/components/icons/LockIcon';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { SerializedStyles } from '@emotion/react';
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
  customCSS: (SerializedStyles | null)[];
  isMyFeedback: boolean;
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
  isMyFeedback = false,
}: UserFeedbackBox) {
  const theme = useAppTheme();

  return (
    <FeedbackBoxBackGround type={type} customCSS={customCSS}>
      <FeedbackBoxHeader
        userName={userName + (isMyFeedback ? ' (나)' : '')}
        type={type}
        feedbackId={feedbackId}
      />
      <div css={isSecret ? secretText(theme) : undefined}>
        {isSecret ? (
          isMyFeedback ? (
            <FeedbackText type={type} text={content} />
          ) : (
            <p>비밀글입니다.</p>
          )
        ) : (
          <FeedbackText type={type} text={content} />
        )}
        {isSecret && <LockIcon />}
      </div>

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
