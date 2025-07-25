import EmptyHeartIcon from '../../../components/icons/EmptyHeartIcon';
import FillHeartIcon from '../../../components/icons/FillHeartIcon';
import Button from '@/components/@commons/Button/Button';
import useLikeButtonManager from '@/domains/hooks/useLikeButtonManager';

interface LikeButtonProps {
  like: boolean | undefined;
  feedbackId: number | undefined;
  likeCount: number;
}

export default function LikeButton({
  like,
  feedbackId,
  likeCount,
}: LikeButtonProps) {
  const { handleLikeButton, isLiked, tempLikeCount } = useLikeButtonManager({
    like,
    feedbackId,
    likeCount,
  });

  return (
    <div>
      <Button onClick={handleLikeButton}>
        {isLiked ? <FillHeartIcon /> : <EmptyHeartIcon />}
      </Button>
      {tempLikeCount}
    </div>
  );
}
