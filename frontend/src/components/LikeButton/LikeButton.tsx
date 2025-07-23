import { useState } from 'react';
import EmptyHeartIcon from '../icons/EmptyHeartIcon';
import FillHeartIcon from '../icons/FillHeartIcon';

interface LikeButtonProps {
  like: boolean | undefined;
}

export default function LikeButton({ like }: LikeButtonProps) {
  const [isLiked, setIsLiked] = useState(like);

  const handleLikeButton = () => {
    if (like === null || like === undefined) return;
    setIsLiked((prev) => !prev);
  };

  return (
    <button onClick={handleLikeButton}>
      {isLiked ? <FillHeartIcon /> : <EmptyHeartIcon />}
    </button>
  );
}
