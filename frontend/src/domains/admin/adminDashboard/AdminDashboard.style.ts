import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const dashboardLayout = css`
  display: flex;
  flex-direction: column;
  gap: 24px;
`;

export const aiFloatingButton = (theme: Theme) => css`
  width: 60px;
  height: 60px;
  background-color: ${theme.colors.purple[100]};

  ${theme.typography.pretendard.small};
  color: ${theme.colors.white[300]};

  &:hover {
    background-color: ${theme.colors.purple[100]}aa;
  }
`;
