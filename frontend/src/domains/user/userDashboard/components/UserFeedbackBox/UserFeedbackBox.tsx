import LockIcon from '@/components/icons/LockIcon';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { SerializedStyles } from '@emotion/react';
import { secretText } from './UserFeedbackBox.styles';
import FeedbackAnswer from '@/domains/components/FeedbackAnswer/FeedbackAnswer';
import { CategoryType } from '@/analytics/types';

interface UserFeedbackBox {
  userName: string;
  type: FeedbackStatusType;
  content: string;
  isLiked: boolean;
  isSecret: boolean;
  postedAt: string;
  feedbackId: number;
  likeCount: number;
  customCSS: (SerializedStyles | null)[];
  isMyFeedback: boolean;
  comment: null | string;
  category: CategoryType;
}

export default function UserFeedbackBox({
  userName,
  type,
  content,
  isLiked,
  isSecret,
  postedAt,
  feedbackId,
  likeCount,
  customCSS,
  isMyFeedback = false,
  comment,
  category,
}: UserFeedbackBox) {
  const theme = useAppTheme();

  return (
    <FeedbackBoxBackGround type={type} customCSS={customCSS}>
      <FeedbackBoxHeader
        userName={userName + (isMyFeedback ? ' (나)' : '')}
        type={type}
        feedbackId={feedbackId}
        category={category}
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
      {(!isSecret || isMyFeedback) && type === 'CONFIRMED' && comment && (
        <FeedbackAnswer answer={comment} />
      )}

      <FeedbackBoxFooter
        type={type}
        isLiked={isLiked}
        postedAt={postedAt}
        isSecret={isSecret}
        feedbackId={feedbackId}
        likeCount={likeCount}
      />
    </FeedbackBoxBackGround>
  );
}
