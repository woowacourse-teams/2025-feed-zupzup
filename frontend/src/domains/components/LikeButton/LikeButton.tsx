import Button from '@/components/@commons/Button/Button';
import useLikeButtonManager from '@/domains/hooks/useLikeButtonManager';
import EmptyHeartIcon from '../../../components/icons/EmptyHeartIcon';
import FillHeartIcon from '../../../components/icons/FillHeartIcon';
import {
  likeButton,
  iconWrapper,
  adminLikeText,
  likeCountText,
} from '@/domains/components/LikeButton/LikeButton.style';

import { useAppTheme } from '@/hooks/useAppTheme';

interface LikeButtonProps {
  like: boolean | undefined;
  feedbackId: number | undefined;
  likeCount: number;
  isAdmin?: boolean;
}

export default function LikeButton({
  like,
  feedbackId,
  likeCount,
  isAdmin,
}: LikeButtonProps) {
  const theme = useAppTheme();
  const { handleLikeButton, isLiked, tempLikeCount } = useLikeButtonManager({
    like,
    feedbackId,
    likeCount,
  });

  if (isAdmin) {
    return (
      <span css={adminLikeText(theme)}>
        <strong css={likeCountText(theme)}>{tempLikeCount}</strong>개의 좋아요
      </span>
    );
  }

  return (
    <Button onClick={handleLikeButton} css={likeButton}>
      <span css={iconWrapper(theme, isLiked ?? false)}>
        {isLiked ? <FillHeartIcon /> : <EmptyHeartIcon />}
      </span>
      {tempLikeCount}
    </Button>
  );
}
