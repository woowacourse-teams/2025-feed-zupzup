import { PAGE_PADDING_PX } from '@/constants';
import AdminHomeHeader from '@/domains/admin/AdminHome/components/AdminHomeHeader/AdminHomeHeader';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export default function AdminHome() {
  const theme = useAppTheme();
  return (
    <div css={homeLayout(theme)}>
      <AdminHomeHeader />
    </div>
  );
}

export const homeLayout = (theme: Theme) => css`
  position: absolute;
  top: -${PAGE_PADDING_PX}px;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  gap: 36px;
  width: 100%;
  height: 100%;
  padding: 52px ${PAGE_PADDING_PX / 2}px 0 ${PAGE_PADDING_PX / 2}px;
  background-color: ${theme.colors.blue[100]};
`;
