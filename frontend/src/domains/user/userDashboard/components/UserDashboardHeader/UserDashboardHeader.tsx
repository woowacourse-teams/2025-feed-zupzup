import {
  headerLayout,
  logoContainer,
  basketIcon,
  logoText,
} from '@/domains/user/userDashboard/components/UserDashboardHeader/UserDashboardHeader.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import basketImage from '@/assets/images/basket.png';
import backgroundImageWebp from '@/assets/images/background.webp';
import backgroundImagePng from '@/assets/images/background.png';
import GhostButton from '@/components/GhostButton/GhostButton';
import ProfileIcon from '@/components/icons/ProfileIcon';
import useNavigation from '@/domains/hooks/useNavigation';

export default function UserDashboardHeader() {
  const theme = useAppTheme();
  const { goPath } = useNavigation();

  const handleNavigateToOnboarding = () => {
    goPath('/');
  };

  return (
    <div css={headerLayout(backgroundImagePng, backgroundImageWebp)}>
      <div css={logoContainer}>
        <img src={basketImage} alt='basket' css={basketIcon} />
        <p css={logoText(theme)}>FEEDZUPZUP</p>
      </div>
      <GhostButton
        icon={<ProfileIcon />}
        text='관리자 이용하기'
        onClick={handleNavigateToOnboarding}
      />
    </div>
  );
}
