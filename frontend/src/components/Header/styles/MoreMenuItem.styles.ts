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
  width: 100%;

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

export const progressBackground = (
  theme: Theme,
  progress: number,
  disabled: boolean
) => css`
  position: absolute;
  left: 0;
  top: 0;
  width: ${disabled ? progress : 0}%;
  height: 100%;
  background: ${theme.colors.gray[200]};

  transition:
    width 0.35s ease-out,
    opacity 0.2s ease;

  opacity: ${disabled ? 1 : 0};
`;

export const progressOverlay = css`
  position: relative;
  display: flex;
  width: 100%;
`;
