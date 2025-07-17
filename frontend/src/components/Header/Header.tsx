import { css } from '@emotion/react';

import Glitter from '../icons/Glitter';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import ArrowLeft from '../ArrowLeftButton/ArrowLeft';

interface HeaderProps {
  title: string;
  subtitle: string;
}

export default function Header({ title, subtitle }: HeaderProps) {
  const theme = useAppTheme();

  return (
    <header css={header}>
      <div css={headerSection}>
        <ArrowLeft onClick={() => console.log('뒤로가기 버튼 클릭')} />
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

const header = css`
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 64px;
  padding: 20px;
  background-color: white;
`;

const headerSection = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const headerTitle = (theme: Theme) => css`
  font-size: 14px;
  color: ${theme.colors.darkGray[400]};
`;

const headerSubtitle = (theme: Theme) => css`
  color: ${theme.colors.gray[600]};
`;

const captionSection = css`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;
