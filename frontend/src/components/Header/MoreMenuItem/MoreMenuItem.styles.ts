import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const moreMenuItemContainer = css`
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px;
  cursor: pointer;
`;

export const moreMenuItemText = (theme: Theme) => css`
  ${theme.typography.pretendard.micro}
`;
