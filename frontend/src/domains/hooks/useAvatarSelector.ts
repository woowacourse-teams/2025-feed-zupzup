import { userImages } from '@/utils/getUserImages';
import { useEffect, useState } from 'react';

interface ImagePair {
  webp: string;
  png: string;
}

interface UseAvatarSelectorProps {
  feedbackId: number;
}

export default function useAvatarSelector({
  feedbackId,
}: UseAvatarSelectorProps) {
  const [userImage, setUserImage] = useState<ImagePair>(userImages[0]); // 기본값 설정

  useEffect(() => {
    const imgId = feedbackId % userImages.length;
    setUserImage(userImages[imgId]);
  }, [feedbackId]);

  return userImage;
}
