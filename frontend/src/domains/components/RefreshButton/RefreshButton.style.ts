import { Theme } from '@/theme';
import { css, keyframes } from '@emotion/react';

const glow = keyframes`
  0% {
    filter: invert(20%) brightness(100%) drop-shadow(0 0 0px rgba(255,255,255,0));
  }
  50% {
    filter: invert(35%) brightness(180%) drop-shadow(0 0 6px rgba(255,255,255,0.6));
  }
  100% {
    filter: invert(20%) brightness(100%) drop-shadow(0 0 0px rgba(255,255,255,0));
  }
`;
export const glowButtonStyle = css`
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  border-radius: 50%;
  animation: ${glow} 1.2s ease-in-out infinite;
`;

export const buttonBackgroundStyle = (theme: Theme) => css`
  &:hover {
    background-color: ${theme.colors.gray[100]}aa;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    border-radius: 50%;
    cursor: pointer;
  }
`;

export const refreshButtonLayout = css`
  position: relative;
  width: 40px;
  height: 40px;
  cursor: pointer;
`;

export const feedbackDiffStyle = (
  theme: Theme,
  hasMoreFeedback: boolean
) => css`
  transition: all 0.3s ease;
  position: absolute;
  top: 13px;
  left: ${hasMoreFeedback ? '12px' : '16px'};
  color: ${theme.colors.darkGray[100]};
  animation: ${glow} 1.2s ease-in-out infinite;
`;
