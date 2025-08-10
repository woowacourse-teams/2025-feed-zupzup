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
import { HEADER_EXCEPT_PATHS, HEADER_CONFIGS } from '@/constants/headerConfig';
import ArrowLeftIcon from '../icons/ArrowLeftIcon';

export default function Header() {
  const location = useLocation();
  const theme = useAppTheme();

  if (HEADER_EXCEPT_PATHS.includes(location.pathname)) {
    return null;
  }

  const { title, subtitle, showMoreIcon, showBackButton } =
    HEADER_CONFIGS[location.pathname];

  return (
    <header css={header(theme)}>
      <div css={arrowTitleContainer}>
        {showBackButton && (
          <Button onClick={() => {}}>
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
        <Button onClick={() => {}}>
          <MoreVerticalIcon />
        </Button>
      )}
    </header>
  );
}
