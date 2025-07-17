import { css } from '@emotion/react';
import heroBackground from '@/assets/images/hero-background.png';

import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';

export const hero = (theme: Theme) => css`
  background-image:
    linear-gradient(${theme.colors.black[100]}59, ${theme.colors.black[100]}59),
    url(${heroBackground});
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  color: white;
  text-align: center;
  margin: -14px -14px 0 -14px;
  box-sizing: border-box;
`;

export const heroHeader = (theme: Theme) => css`
  width: 100%;
  height: 56px;
  background-color: ${theme.colors.black[100]}33;
  margin: 0 -20px 0 -20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 14px;
`;

export const heroContent = css`
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 0 8px 20px 8px;
  width: 100%;
  padding: 0 20px;
`;

export const heroHr = (theme: Theme) => css`
  width: 56px;
  height: 2px;
  background-color: ${theme.colors.yellow[200]};
  border: none;
  margin: 0 0 14px 0;
`;

export const heroTitle = (theme: Theme) => css`
  ${theme.typography.inter.small};
  color: ${theme.colors.yellow[200]};
  margin: 0 0 14px 0;
`;

export const heroLogo = css`
  width: 112px;
  height: auto;
  margin: 0 0 16px 0;
`;

export const heroDescription = (theme: Theme) => css`
  ${theme.typography.inter.small};
  color: ${theme.colors.white[100]};
  margin: 0 0 24px 0;
  text-align: left;
  line-height: 1.5;
`;

export const catAnimation = css`
  position: absolute;
  width: 60px;
  height: auto;
  bottom: 32px;
  right: 24px;
  animation: catMove 3s ease-in-out infinite;
  z-index: 1;

  @keyframes catMove {
    0% {
      transform: translateX(0);
    }
    50% {
      transform: translateX(-100px);
    }
    100% {
      transform: translateX(0);
    }
  }
`;
