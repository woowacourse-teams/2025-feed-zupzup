import { useAppTheme } from '@/hooks/useAppTheme';
import GlitterIcon from '../icons/GlitterIcon';
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
        <div css={captionSection}>
          <p css={headerTitle(theme)}>{title}</p>
          <p css={headerSubtitle(theme)}>{subtitle}</p>
        </div>
      </div>
      <GlitterIcon />
    </header>
  );
}
