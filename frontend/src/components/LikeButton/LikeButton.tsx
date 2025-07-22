import { useState } from 'react';
import EmptyHeartIcon from '../icons/EmptyHeartIcon';
import FillHeartIcon from '../icons/FillHeartIcon';

interface LikeButtonProps {
  like: boolean;
}

export default function LikeButton({ like = false }: LikeButtonProps) {
  const [isLiked, setIsLiked] = useState(like);

  const handleLikeButton = () => setIsLiked((prev) => !prev);

  return (
    <button onClick={handleLikeButton}>
      {isLiked ? <FillHeartIcon /> : <EmptyHeartIcon />}
    </button>
  );
}
