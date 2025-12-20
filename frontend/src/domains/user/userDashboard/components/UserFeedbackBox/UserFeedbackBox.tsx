import LockIcon from '@/components/icons/LockIcon';
import { CategoryListType } from '@/constants/categoryList';
import FeedbackAnswer from '@/domains/components/FeedbackAnswer/FeedbackAnswer';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackBoxHeader from '@/domains/components/FeedbackBoxHeader/FeedbackBoxHeader';

import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { memo } from 'react';
import { highlightStyle } from '../../UserDashboard.style';
import { secretText } from './UserFeedbackBox.styles';
import FeedbackContent from '@/domains/components/FeedbackText/FeedbackContent';

interface UserFeedbackBox {
  userName: string;
  type: FeedbackStatusType;
  content: string;
  isLiked: boolean;
  isSecret: boolean;
  postedAt: string;
  feedbackId: number;
  likeCount: number;
  isHighlighted: boolean;
  isMyFeedback: boolean;
  comment: null | string;
  category: CategoryListType;
  imgUrl: string | null;
}

export default memo(function UserFeedbackBox({
  userName,
  type,
  content,
  isLiked,
  isSecret,
  postedAt,
  feedbackId,
  likeCount,
  isHighlighted,
  isMyFeedback = false,
  comment,
  category,
  imgUrl,
}: UserFeedbackBox) {
  const theme = useAppTheme();

  return (
    <FeedbackBoxBackGround
      type={type}
      customCSS={[isHighlighted ? highlightStyle : null]}
    >
      <FeedbackBoxHeader
        userName={userName + (isMyFeedback ? ' (나)' : '')}
        type={type}
        feedbackId={feedbackId}
        category={category}
      />
      <div css={isSecret ? secretText(theme) : undefined}>
        {isSecret ? (
          isMyFeedback ? (
            <FeedbackContent text={content} imgUrl={imgUrl} />
          ) : (
            <p>비밀글입니다.</p>
          )
        ) : (
          <FeedbackContent text={content} imgUrl={imgUrl} />
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
});
