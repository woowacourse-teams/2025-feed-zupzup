import { useAppTheme } from '@/hooks/useAppTheme';
import MoreVerticalIcon from '../icons/MoreVerticalIcon';
import {
  arrowTitleContainer,
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
} from './Header.style';
import { useLocation } from 'react-router-dom';
import Button from '../@commons/Button/Button';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';
import ArrowLeftIcon from '../icons/ArrowLeftIcon';
import { useNavigate } from 'react-router-dom';

export default function Header() {
  const location = useLocation();
  const theme = useAppTheme();
  const navigate = useNavigate();

  const { title, subtitle, showMoreIcon, showBackButton } =
    LAYOUT_CONFIGS[location.pathname].header;

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  const handleMoreButtonClick = () => {
    // TODO: 더보기 메뉴 로직 구현 여기서 하심 됩니당.
  };

  return (
    <header css={header(theme)}>
      <div css={arrowTitleContainer}>
        {showBackButton && (
          <Button onClick={handleBackButtonClick}>
            <ArrowLeftIcon color={theme.colors.white[100]} />
          </Button>
        )}
        <div css={headerSection}>
          <div css={captionSection}>
            <p css={headerTitle(theme)}>{title}</p>
            <p css={headerSubtitle(theme)}>{subtitle}</p>
          </div>
        </div>
      </div>
      {showMoreIcon && (
        <Button onClick={handleMoreButtonClick}>
          <MoreVerticalIcon />
        </Button>
      )}
    </header>
  );
}
