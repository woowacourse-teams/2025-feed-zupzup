import { useAppTheme } from '@/hooks/useAppTheme';
import MoreVerticalIcon from '../icons/MoreVerticalIcon';
import {
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
} from './Header.style';
import { useLocation } from 'react-router-dom';
import Button from '../@commons/Button/Button';
import {
  HEADER_EXCEPT_PATHS,
  HEADER_CONFIGS,
  DEFAULT_HEADER_CONFIG,
} from '@/constants/headerConfig';

export default function Header() {
  const location = useLocation();
  const theme = useAppTheme();

  const getHeaderConfig = () => {
    return HEADER_CONFIGS[location.pathname] || DEFAULT_HEADER_CONFIG;
  };

  const { title, subtitle, showMoreIcon } = getHeaderConfig();

  if (HEADER_EXCEPT_PATHS.includes(location.pathname)) {
    return null;
  }

  return (
    <header css={header(theme)}>
      <div css={headerSection}>
        <div css={captionSection}>
          <p css={headerTitle(theme)}>{title}</p>
          <p css={headerSubtitle(theme)}>{subtitle}</p>
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
