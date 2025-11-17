import Button from '@/components/@commons/Button/Button';

import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { useState } from 'react';
import {
  feedbackImage,
  feedbackImageButton,
  feedbackText,
  feedbackTextContainer,
} from './FeedbackContent.styles';
import SmallTriangleIcon from '@/components/icons/SmallTriangleIcon';

export interface FeedbackContentProps {
  text: string;
  type: FeedbackStatusType;
  imgUrl: string | null;
}

export default function FeedbackContent({
  text,
  type,
  imgUrl,
}: FeedbackContentProps) {
  const theme = useAppTheme();

  const [showImg, setShowImg] = useState(false);
  const [isLeaving, setIsLeaving] = useState(false);
  const handleShowImg = () => {
    if (showImg) {
      setShowImg(false);

      setIsLeaving(true);
      setTimeout(() => {
        setIsLeaving(false);
      }, 350);
    } else {
      setShowImg(true);
    }
  };

  return (
    <div css={feedbackTextContainer}>
      <p css={feedbackText(theme, type)}>{text}</p>

      {imgUrl && (
        <Button
          onClick={handleShowImg}
          css={feedbackImageButton(theme, showImg)}
          aria-expanded={showImg}
          aria-label={showImg ? '첨부 이미지 접기' : '첨부 이미지 펼치기'}
        >
          {showImg ? (
            <SmallTriangleIcon />
          ) : (
            <SmallTriangleIcon style={{ transform: 'rotate(90deg)' }} />
          )}
          <p>첨부 이미지 </p>
        </Button>
      )}
      {(showImg || isLeaving) && imgUrl && (
        <img
          css={feedbackImage(showImg && !isLeaving)}
          src={imgUrl}
          alt='피드백 첨부 이미지'
        />
      )}
    </div>
  );
}
