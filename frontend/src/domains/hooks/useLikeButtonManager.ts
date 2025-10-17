import { ApiError } from '@/apis/apiClient';
import { deleteLike, postLike } from '@/apis/userFeedback.api';
import useMyLikedFeedback from '@/domains/user/userDashboard/hooks/useMyLikedFeedback';
import { useErrorModal } from '@/hooks/useErrorModal';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

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
  const [optimisticLike, setOptimisticLike] = useState<boolean | null>(null);
  const [optimisticCount, setOptimisticCount] = useState<number | null>(null);

  const isLiked = optimisticLike ?? like;
  const tempLikeCount = optimisticCount ?? likeCount;

  const { showErrorModal } = useErrorModal();
  const { refetchMyLikeFeedbackIds } = useMyLikedFeedback();

  const { mutate: likeMutation } = useMutation({
    mutationFn: postLike,
    onError: (e: ApiError) => {
      setOptimisticLike(null);
      setOptimisticCount(null);
      showErrorModal(e, '에러');
    },
    onSettled: () => {
      refetchMyLikeFeedbackIds();
    },
  });

  const { mutate: deleteLikeMutation } = useMutation({
    mutationFn: deleteLike,
    onError: (e: ApiError) => {
      setOptimisticLike(null);
      setOptimisticCount(null);
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
      setOptimisticCount(tempLikeCount + 1);
      setOptimisticLike(true);
      likeMutation({ feedbackId });
    } else {
      setOptimisticCount(tempLikeCount - 1);
      setOptimisticLike(false);
      deleteLikeMutation({ feedbackId });
    }
  };

  return {
    tempLikeCount,
    isLiked,
    handleLikeButton,
  };
}
