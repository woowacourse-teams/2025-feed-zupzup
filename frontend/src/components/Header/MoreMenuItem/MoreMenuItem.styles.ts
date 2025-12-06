import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const moreMenuItemContainer = (theme: Theme, disabled: boolean) => css`
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px;
  cursor: ${disabled ? 'not-allowed' : 'pointer'};
  opacity: ${disabled ? 0.5 : 1};
  pointer-events: ${disabled ? 'none' : 'auto'};

  &:hover {
    background-color: ${!disabled && theme.colors.gray[100]};
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
