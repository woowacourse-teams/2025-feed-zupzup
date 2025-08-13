import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const container = (theme: Theme) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  gap: 10px;
  height: 30px;
  font-size: 14px;
  background-color: ${theme.colors.purple[100]}10;
`;
