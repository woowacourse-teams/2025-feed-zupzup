import IMG1 from '@/assets/images/user1.png';
import LockIcon from '@/components/icons/LockIcon';
import FeedbackBoxBackGround from '@/domains/components/FeedbackBoxBackGround/FeedbackBoxBackGround';
import FeedbackBoxFooter from '@/domains/components/FeedbackBoxFooter/FeedbackBoxFooter';
import FeedbackText from '@/domains/components/FeedbackText/FeedbackText';
import { userImages } from '@/domains/user/dashboard/utils/getUserImages';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { SerializedStyles } from '@emotion/react';
import { useEffect, useState } from 'react';
import {
  imgContainer,
  imgLayout,
  secretText,
  userNameStyle,
} from './UserFeedbackBox.styles';

interface UserFeedbackBox {
  userName?: string;
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
  const [userImage, setUserImage] = useState(IMG1);

  useEffect(() => {
    const random = Math.floor(Math.random() * userImages.length);
    setUserImage(userImages[random]);
  }, []);

  return (
    <FeedbackBoxBackGround type={type} customCSS={customCSS}>
      <div css={imgContainer}>
        <img src={userImage} alt='user icon' css={imgLayout} />
        <p css={userNameStyle(theme)}>{userName}</p>
      </div>
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
        isLiked={isLiked}
        createdAt={createdAt}
        isSecret={isSecret}
        feedbackId={feedbackId}
        likeCount={likeCount}
      />
    </FeedbackBoxBackGround>
  );
}
