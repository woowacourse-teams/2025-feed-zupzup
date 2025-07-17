import { useAppTheme } from '@/hooks/useAppTheme';
import ArrowLeftButton from '../ArrowLeftButton/ArrowLeftButton';
import Glitter from '../icons/Glitter';
import {
  captionSection,
  header,
  headerSection,
  headerSubtitle,
  headerTitle,
} from './Header.style';
import { HeaderProps } from './Header.type';

export default function Header({ title, subtitle }: HeaderProps) {
  const theme = useAppTheme();

  return (
    <header css={header}>
      <div css={headerSection}>
        <ArrowLeftButton onClick={() => console.log('뒤로가기 버튼 클릭')} />
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
