import {
  headerLayout,
  homeCaptionContainer,
  logoContainer,
  basketIcon,
  logoText,
  greetingContainer,
  greetingBold,
  greetingLight,
} from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import basketImage from '@/assets/images/basket.png';
import useNavigation from '@/domains/hooks/useNavigation';

export default function AdminHomeHeader() {
  const theme = useAppTheme();
  const { goPath } = useNavigation();

  const handleNavigateToHome = () => {
    goPath('/');
  };

  return (
    <div css={headerLayout()}>
      <div css={homeCaptionContainer(theme)}>
        <div css={logoContainer} onClick={handleNavigateToHome}>
          <img src={basketImage} alt='basket' css={basketIcon} />
          <p css={logoText(theme)}>FEEDZUPZUP</p>
        </div>
        <div css={greetingContainer}>
          <p css={greetingBold(theme)}>반가워요!</p>
          <p css={greetingLight(theme)}>피드백 방을 관리해보세요</p>
        </div>
      </div>
    </div>
  );
}
