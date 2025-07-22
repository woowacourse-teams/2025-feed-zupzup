import { useAppTheme } from '@/hooks/useAppTheme';
import GlitterIcon from '../icons/GlitterIcon';
import IconButton from '../IconButton/IconButton';
import ArrowLeftIcon from '../icons/ArrowLeftIcon';
import {
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
} from './Header.style';
import { useNavigate } from 'react-router-dom';

export interface HeaderProps {
  title: string;
  subtitle: string;
}

export default function Header({ title, subtitle }: HeaderProps) {
  const theme = useAppTheme();
  const navigate = useNavigate();

  return (
    <header css={header}>
      <div css={headerSection}>
        <IconButton icon={<ArrowLeft />} onClick={() => navigate(-1)} />
        <div css={captionSection}>
          <p css={[headerTitle(theme), theme.typography.inter.bodyBold]}>
            {title}
          </p>
          <p css={[headerSubtitle(theme), theme.typography.inter.caption]}>
            {subtitle}
          </p>
        </div>
      </div>
      <GlitterIcon />
    </header>
  );
}
