import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const floatingButton = (theme: Theme) => css`
  position: sticky;
  bottom: 32px;
  left: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
  width: 60px;
  height: 60px;
  background-color: ${theme.colors.purple[100]};
  border-radius: 50%;
  cursor: pointer;
  transition:
    bottom 0.3s ease,
    background-color 0.2s ease;

  &:hover {
    background-color: ${theme.colors.purple[100]}aa;
  }
`;

export const floatingButtonIcon = css`
  width: 16px;
  height: 16px;
`;
