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

  if (LAYOUT_CONFIGS[location.pathname] === undefined) return null;
  const { title, subtitle, showMoreIcon, showBackButton } =
    LAYOUT_CONFIGS[location.pathname].header;

  return (
    <header css={header(theme)}>
      <div css={arrowTitleContainer}>
        {showBackButton && (
          <Button
            onClick={() => {
              navigate(-1);
            }}
          >
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
