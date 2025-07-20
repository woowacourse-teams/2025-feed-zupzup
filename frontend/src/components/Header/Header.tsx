import { useAppTheme } from '@/hooks/useAppTheme';
import Glitter from '../icons/Glitter';

import IconButton from '../IconButton/IconButton';
import ArrowLeft from '../icons/ArrowLeft';
import {
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
} from './Header.style';

export interface HeaderProps {
  title: string;
  subtitle: string;
}

export default function Header({ title, subtitle }: HeaderProps) {
  const theme = useAppTheme();

  return (
    <header css={header}>
      <div css={headerSection}>
        <IconButton icon={<ArrowLeft />} />
        <div css={captionSection}>
          <p css={[headerTitle(theme), theme.typography.inter.bodyBold]}>
            {title}
          </p>
          <p css={[headerSubtitle(theme), theme.typography.inter.caption]}>
            {subtitle}
          </p>
        </div>
      </div>
      <Glitter />
    </header>
  );
}
