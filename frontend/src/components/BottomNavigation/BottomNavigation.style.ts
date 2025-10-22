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
  gap: 40px;
  width: 100%;
  max-width: 600px;
  height: 66px;
  padding: 26px;
  padding-bottom: calc(26px + env(safe-area-inset-bottom));
  background-color: ${theme.colors.white[100]};
  transform: translateX(-50%);
  border-top: 1px solid ${theme.colors.gray[100]};

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
  gap: 4px;
  width: calc(50% - 20px);
  min-height: 44px;
  padding: 8px 0;
  background-color: ${isActive
    ? `${theme.colors.purple[100]}0D`
    : 'transparent'};
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
`;

export const navTextStyle = (theme: Theme, isActive: boolean) => css`
  font-size: 12px;
  font-weight: 500;
  color: ${isActive ? theme.colors.purple[100] : theme.colors.gray[600]};
  user-select: none;
`;
