import { ApiError } from '@/apis/apiClient';
import { deleteLike, postLike } from '@/apis/userFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

interface useLikeButtonManagerProps {
  like: boolean | undefined;
  feedbackId: number | undefined;
  likeCount: number;
}

export default function useLikeButtonManager({
  like,
  feedbackId,
  likeCount,
}: useLikeButtonManagerProps) {
  const [isLiked, setIsLiked] = useState(like);
  const [tempLikeCount, setTempLikeCount] = useState(likeCount);
  const { showErrorModal } = useErrorModalContext();

  const addLikeFeedbackIds = (feedbackId: number) => {
    const likeFeedbackIds = getLocalStorage<number[]>('feedbackIds') ?? [];
    likeFeedbackIds.push(feedbackId);

    setLocalStorage<number[]>('feedbackIds', likeFeedbackIds);
  };

  const removeLikeFeedbackIds = (feedbackId: number) => {
    const likeFeedbackIds = getLocalStorage<number[]>('feedbackIds') ?? [];
    const filteredFeedbackIds = likeFeedbackIds.filter(
      (id) => id !== feedbackId
    );

    setLocalStorage<number[]>('feedbackIds', filteredFeedbackIds);
  };

  const { mutate: likeMutation } = useMutation({
    mutationFn: postLike,
    onSuccess: () => {
      addLikeFeedbackIds(feedbackId || 0);
      setIsLiked(true);
    },
    onError: (e: ApiError) => {
      setIsLiked(false);
      setTempLikeCount((prev) => prev - 1);
      showErrorModal(e, '에러');
    },
  });

  const { mutate: deleteLikeMutation } = useMutation({
    mutationFn: deleteLike,
    onSuccess: () => {
      removeLikeFeedbackIds(feedbackId || 0);
      setIsLiked(false);
    },
    onError: (e: ApiError) => {
      setIsLiked(false);
      setTempLikeCount((prev) => prev + 1);
      showErrorModal(e, '에러');
    },
  });

  const handleLikeButton = async () => {
    if (like === null || like === undefined) return;
    if (feedbackId === undefined) return;

    if (!isLiked) {
      setTempLikeCount((prev) => prev + 1);
      likeMutation({ feedbackId });
    } else {
      setTempLikeCount((prev) => prev - 1);
      deleteLikeMutation({ feedbackId });
    }
  };

  return {
    tempLikeCount,
    isLiked,
    handleLikeButton,
  };
}
