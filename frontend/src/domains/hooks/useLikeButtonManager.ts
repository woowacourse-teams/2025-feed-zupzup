import { deleteLike, postLike } from '@/apis/userFeedback.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { getLocalStorage, setLocalStorage } from '@/utils/localStorage';
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

  const handleLikeButton = async () => {
    if (like === null || like === undefined) return;
    if (feedbackId === undefined) return;

    if (!isLiked) {
      setTempLikeCount((prev) => prev + 1);
      setIsLiked(true);
      try {
        await postLike({
          feedbackId,
          onSuccess: () => addLikeFeedbackIds(feedbackId),
          onError: () => {
            setIsLiked(false);
            setTempLikeCount((prev) => prev - 1);
          },
        });
      } catch (e) {
        showErrorModal(e, '에러');
      }
    } else {
      setTempLikeCount((prev) => prev - 1);
      setIsLiked(false);
      try {
        await deleteLike({
          feedbackId,
          onSuccess: () => removeLikeFeedbackIds(feedbackId),
          onError: () => {
            setIsLiked(false);
            setTempLikeCount((prev) => prev + 1);
          },
        });
      } catch (e) {
        showErrorModal(e, '에러');
      }
    }
  };

  return {
    tempLikeCount,
    isLiked,
    handleLikeButton,
  };
}
