import { useState } from 'react';
import EmptyHeartIcon from '../icons/EmptyHeartIcon';
import FillHeart from '../icons/FillHeart';

interface LikeButtonProps {
  like: boolean;
}

export default function LikeButton({ like = false }: LikeButtonProps) {
  const [isLiked, setIsLiked] = useState(like);

  const handleLikeButton = () => setIsLiked((prev) => !prev);

  return (
    <button onClick={handleLikeButton}>
      {isLiked ? <FillHeart /> : <EmptyHeartIcon />}
    </button>
  );
}
