import { useAppTheme } from '@/hooks/useAppTheme';
import MoreVerticalIcon from '../icons/MoreVerticalIcon';
import {
  arrowTitleContainer,
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
  MoreButton,
  moreMenu,
  moreMenuContainer,
} from './Header.style';

import Button from '../@commons/Button/Button';

import ArrowLeftIcon from '../icons/ArrowLeftIcon';
import MoreMenu from '@/components/Header/MoreMenu/MoreMenu';
import useMoreMenuManager from '@/components/Header/hooks/useMoreMenuManager';
import { useLayoutConfig } from '@/hooks/useLayoutConfig';
import useNavigation from '@/domains/hooks/useNavigation';

export default function Header() {
  const theme = useAppTheme();
  const { goBack } = useNavigation();
  const { layoutConfig } = useLayoutConfig();

  const { isOpenMoreMenu, toggleMoreMenu, moreButtonRef, closeMoreMenu } =
    useMoreMenuManager();

  const { title, subtitle, hasMoreIcon, showBackButton } = layoutConfig.header;

  return (
    <header css={header(theme)}>
      <div css={arrowTitleContainer}>
        {showBackButton && (
          <Button onClick={goBack}>
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
          <Button onClick={toggleMoreMenu} customCSS={MoreButton}>
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
