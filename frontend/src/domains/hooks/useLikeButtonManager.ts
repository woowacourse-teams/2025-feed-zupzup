import { ApiError } from '@/apis/apiClient';
import { deleteLike, postLike } from '@/apis/userFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import useMyLikedFeedback from '@/domains/user/userDashboard/hooks/useMyLikedFeedback';
import { useMutation } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

interface useLikeButtonManagerProps {
  like: boolean;
  feedbackId: number | undefined;
  likeCount: number;
}

export default function useLikeButtonManager({
  like,
  feedbackId,
  likeCount,
}: useLikeButtonManagerProps) {
  const [isLiked, setIsLiked] = useState<boolean>(false);
  const [tempLikeCount, setTempLikeCount] = useState<number>(0);
  const { showErrorModal } = useErrorModalContext();
  const { refetchMyLikeFeedbackIds } = useMyLikedFeedback();

  const { mutate: likeMutation } = useMutation({
    mutationFn: postLike,
    onError: (e: ApiError) => {
      setIsLiked(false);
      setTempLikeCount((prev) => prev - 1);
      showErrorModal(e, '에러');
    },
    onSettled: () => {
      refetchMyLikeFeedbackIds();
    },
  });

  const { mutate: deleteLikeMutation } = useMutation({
    mutationFn: deleteLike,
    onError: (e: ApiError) => {
      setIsLiked(true);
      setTempLikeCount((prev) => prev + 1);
      showErrorModal(e, '에러');
    },
    onSettled: () => {
      refetchMyLikeFeedbackIds();
    },
  });

  const handleLikeButton = async () => {
    if (like === null || like === undefined) return;
    if (feedbackId === undefined) return;

    if (!isLiked) {
      setTempLikeCount((prev) => prev + 1);
      setIsLiked(true);
      likeMutation({ feedbackId });
    } else {
      setTempLikeCount((prev) => prev - 1);
      setIsLiked(false);
      deleteLikeMutation({ feedbackId });
    }
  };

  useEffect(() => {
    setIsLiked(like);
  }, [like]);

  useEffect(() => {
    setTempLikeCount(likeCount);
  }, [likeCount]);

  return {
    tempLikeCount,
    isLiked,
    handleLikeButton,
  };
}
