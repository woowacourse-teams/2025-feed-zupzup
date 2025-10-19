import {
  headerLayout,
  logoContainer,
  basketIcon,
  logoText,
} from '@/domains/user/userDashboard/components/UserDashboardHeader/UserDashboardHeader.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import basketImage from '@/assets/images/basket.png';
import backgroundImage from '@/assets/images/background.png';
import useNavigation from '@/domains/hooks/useNavigation';
import { Analytics, userDashboardEvents } from '@/analytics';

export default function UserDashboardHeader() {
  const theme = useAppTheme();

  const { goPath } = useNavigation();

  const handleNavigateToOnboarding = () => {
    Analytics.track(userDashboardEvents.viewSuggestionsFromDashboard());
    goPath('/');
  };

  return (
    <div css={headerLayout(backgroundImage)}>
      <div css={logoContainer} onClick={handleNavigateToOnboarding}>
        <img src={basketImage} alt='basket' css={basketIcon} />
        <p css={logoText(theme)}>FEEDZUPZUP</p>
      </div>
    </div>
  );
}
