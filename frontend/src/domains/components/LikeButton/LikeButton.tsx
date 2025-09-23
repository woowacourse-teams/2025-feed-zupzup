import Button from '@/components/@commons/Button/Button';
import useLikeButtonManager from '@/domains/hooks/useLikeButtonManager';
import EmptyHeartIcon from '../../../components/icons/EmptyHeartIcon';
import FillHeartIcon from '../../../components/icons/FillHeartIcon';
import {
  likeButton,
  iconWrapper,
} from '@/domains/components/LikeButton/LikeButton.style';
import { theme } from '@/theme';

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
    <Button onClick={handleLikeButton} css={likeButton}>
      <span css={iconWrapper(theme, isLiked ?? false)}>
        {isLiked ? <FillHeartIcon /> : <EmptyHeartIcon />}
      </span>
      {tempLikeCount}
    </Button>
  );
}
