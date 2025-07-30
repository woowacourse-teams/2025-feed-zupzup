import IMG1 from '@/assets/images/avatar1.png';
import { userImages } from '@/utils/getUserImages';
import { useEffect, useState } from 'react';

interface UseRandomAvatarProps {
  feedbackId: number;
}

export default function useRandomAvatar({ feedbackId }: UseRandomAvatarProps) {
  const [userImage, setUserImage] = useState(IMG1);

  useEffect(() => {
    const random = feedbackId % userImages.length;
    setUserImage(userImages[random]);
  }, []);

  return userImage;
}
