import { useAppTheme } from '@/hooks/useAppTheme';
import {
  arrowTitleContainer,
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
} from './Header.style';

import Button from '../@commons/Button/Button';

import ArrowLeftIcon from '../icons/ArrowLeftIcon';
import HeaderMoreIcon from './HeaderMoreIcon/HeaderMoreIcon';
import { useLayoutConfig } from '@/hooks/useLayoutConfig';
import useNavigation from '@/domains/hooks/useNavigation';

export default function Header() {
  const theme = useAppTheme();
  const { goBack } = useNavigation();
  const { layoutConfig } = useLayoutConfig();

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
      {hasMoreIcon && <HeaderMoreIcon />}
    </header>
  );
}
