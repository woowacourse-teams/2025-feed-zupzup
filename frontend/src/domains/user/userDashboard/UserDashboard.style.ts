import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const dashboardLayout = css`
  display: flex;
  flex-direction: column;
  gap: 24px;
`;

export const goOnboardButton = (theme: Theme) => css`
  width: 60px;
  height: 60px;
  background-color: ${theme.colors.purple[100]};

  &:hover {
    background-color: ${theme.colors.purple[100]}aa;
  }
`;
export const goTopButton = (theme: Theme) => css`
  width: 40px;
  height: 40px;
  background-color: white;
  border: 1px solid ${theme.colors.purple[100]};
`;
