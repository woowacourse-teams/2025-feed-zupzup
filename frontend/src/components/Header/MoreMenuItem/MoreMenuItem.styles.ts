import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const moreMenuItemContainer = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px;
  cursor: pointer;

  &:hover {
    background-color: ${theme.colors.gray[100]};
  }
`;

export const moreMenuItemText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}
`;

export const moreMenuItemIcon = css`
  display: flex;
  align-items: center;
  justify-content: center;
`;
