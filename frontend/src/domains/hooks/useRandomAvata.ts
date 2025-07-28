import IMG1 from '@/assets/images/user1.png';
import { userImages } from '@/domains/user/userDashboard/utils/getUserImages';
import { useEffect, useState } from 'react';

export default function useRandomAvatar() {
  const [userImage, setUserImage] = useState(IMG1);

  useEffect(() => {
    const random = Math.floor(Math.random() * userImages.length);
    setUserImage(userImages[random]);
  }, []);

  return userImage;
}
