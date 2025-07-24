import { useState } from 'react';
import EmptyHeartIcon from '../icons/EmptyHeartIcon';
import FillHeartIcon from '../icons/FillHeartIcon';
import { postLike } from '@/apis/userFeedback.api';
import IconButton from '@/components/IconButton/IconButton';

interface LikeButtonProps {
  like: boolean | undefined;
  feedbackId: number | undefined;
}

export default function LikeButton({ like, feedbackId }: LikeButtonProps) {
  const [isLiked, setIsLiked] = useState(like);

  const handleLikeButton = async () => {
    if (like === null || like === undefined) return;
    if (feedbackId === undefined) return;

    if (!isLiked) {
      const data = await postLike({ feedbackId });
      console.log(data);
      setIsLiked(true);
    }
  };

  return (
    <button onClick={handleLikeButton}>
      {isLiked ? (
        <IconButton onClick={handleLikeButton} icon={<FillHeartIcon />} />
      ) : (
        <IconButton
          onClick={handleLikeButton}
          icon={<EmptyHeartIcon />}
        ></IconButton>
      )}
    </button>
  );
}
