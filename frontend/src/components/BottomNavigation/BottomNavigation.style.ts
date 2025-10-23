import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const bottomNavStyle = (theme: Theme) => css`
  position: fixed;
  bottom: 0;
  left: 50%;
  z-index: 100;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 600px;
  height: 66px;
  padding: 12px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background-color: ${theme.colors.blue[100]};
  transform: translateX(-50%);

  @supports (height: 100dvh) {
    transition: transform 0.3s ease;
  }

  /* &.keyboard-open {
    transform: translateY(100%);
  } */
`;

export const navItemStyle = (theme: Theme, isActive: boolean) => css`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 6px;
  width: calc(50% - 8px);
  min-height: 44px;
  padding: 8px 12px;
  background-color: ${isActive ? theme.colors.blue[200] : 'transparent'};
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
`;

export const navTextStyle = (theme: Theme) => css`
  font-size: 14px;
  font-weight: 400;
  color: ${theme.colors.white[100]};
  user-select: none;
`;
