import IMG1 from '@/assets/images/avatar1.png';
import { userImages } from '@/utils/getUserImages';
import { useEffect, useState } from 'react';

interface UseAvatarSelectorProps {
  feedbackId: number;
}

export default function useAvatarSelector({
  feedbackId,
}: UseAvatarSelectorProps) {
  const [userImage, setUserImage] = useState(IMG1);

  useEffect(() => {
    const imgId = feedbackId % userImages.length;
    setUserImage(userImages[imgId]);
  }, []);

  return userImage;
}
