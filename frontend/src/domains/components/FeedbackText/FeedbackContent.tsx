import Button from '@/components/@commons/Button/Button';

import SmallTriangleIcon from '@/components/icons/SmallTriangleIcon';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { useState } from 'react';
import {
  feedbackImage,
  feedbackImageButton,
  feedbackText,
  feedbackTextContainer,
} from './FeedbackContent.styles';

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

  const [imageStatus, setImageStatus] = useState<'idle' | 'loading' | 'loaded'>(
    'idle'
  );
  const showImg = imageStatus === 'loaded';
  const isLeaving = imageStatus === 'loading';

  const handleShowImg = () => {
    if (imageStatus === 'loaded') {
      setImageStatus('loading');

      setTimeout(() => {
        setImageStatus('idle');
      }, 350);
    } else {
      setImageStatus('loaded');
    }
  };

  return (
    <div css={feedbackTextContainer}>
      <p css={feedbackText(theme, type)}>{text}</p>

      {imgUrl && (
        <Button
          onClick={handleShowImg}
          css={feedbackImageButton(theme, showImg)}
        >
          {showImg ? (
            <SmallTriangleIcon />
          ) : (
            <SmallTriangleIcon style={{ transform: 'rotate(90deg)' }} />
          )}
          <p>첨부 이미지</p>
        </Button>
      )}
      {(showImg || isLeaving) && imgUrl && (
        <img
          css={feedbackImage(showImg && !isLeaving)}
          src={imgUrl}
          alt='첨부 이미지'
        />
      )}
    </div>
  );
}
