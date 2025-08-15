import { useAppTheme } from '@/hooks/useAppTheme';
import MoreVerticalIcon from '../icons/MoreVerticalIcon';
import {
  arrowTitleContainer,
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
  moreMenu,
  moreMenuContainer,
} from './Header.style';
import { useLocation } from 'react-router-dom';
import Button from '../@commons/Button/Button';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';
import ArrowLeftIcon from '../icons/ArrowLeftIcon';
import { useNavigate } from 'react-router-dom';
import MoreMenu from '@/components/Header/MoreMenu/MoreMenu';
import useMoreMenuManager from '@/components/Header/hooks/useMoreMenuManager';

export default function Header() {
  const location = useLocation();
  const theme = useAppTheme();
  const navigate = useNavigate();

  const { isOpenMoreMenu, toggleMoreMenu, moreButtonRef, closeMoreMenu } =
    useMoreMenuManager();

  const { title, subtitle, hasMoreIcon, showBackButton } =
    LAYOUT_CONFIGS[location.pathname].header;

  const handleBackButtonClick = () => {
    navigate(-1);
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
      {hasMoreIcon && (
        <div
          css={moreMenuContainer}
          ref={moreButtonRef as React.RefObject<HTMLDivElement>}
        >
          <Button onClick={toggleMoreMenu}>
            <MoreVerticalIcon />
          </Button>
          {isOpenMoreMenu && (
            <div css={moreMenu}>
              <MoreMenu closeMoreMenu={closeMoreMenu} />
            </div>
          )}
        </div>
      )}
    </header>
  );
}
