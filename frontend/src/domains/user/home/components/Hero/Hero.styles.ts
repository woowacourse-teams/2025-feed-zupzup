import { css } from '@emotion/react';
import heroBackground from '@/assets/images/hero-background.png';
import { Theme } from '@/theme';

export const hero = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
  min-height: 70vh;
  margin: -14px -14px 0;
  text-align: center;
  color: white;
  background-image:
    linear-gradient(${theme.colors.black[100]}59, ${theme.colors.black[100]}59),
    url(${heroBackground});
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
`;

export const heroHeader = (theme: Theme) => css`
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  height: 56px;
  margin: 0 -20px;
  padding: 0 14px;
  background-color: ${theme.colors.black[100]}33;
`;

export const heroContent = css`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  margin: 0 8px 20px;
  padding: 0 20px;
`;

export const heroHr = (theme: Theme) => css`
  width: 56px;
  height: 2px;
  margin: 0 0 14px;
  background-color: ${theme.colors.yellow[200]};
  border: none;
`;

export const heroTitle = (theme: Theme) => css`
  ${theme.typography.inter.small};

  margin: 0 0 14px;
  color: ${theme.colors.yellow[200]};
`;

export const heroLogo = css`
  width: 112px;
  height: auto;
  margin: 0 0 16px;
`;

export const heroDescription = (theme: Theme) => css`
  ${theme.typography.inter.small};

  margin: 0 0 24px;
  line-height: 1.5;
  text-align: left;
  color: ${theme.colors.white[100]};
`;

export const catAnimation = (showSuggestButton: boolean) => css`
  position: absolute;
  right: 24px;
  bottom: ${showSuggestButton ? '32px' : '-28px'};
  z-index: 1;
  width: 60px;
  height: auto;
  animation: cat-move ${showSuggestButton ? '3s' : '9s'} ease-in-out infinite;

  @keyframes cat-move {
    0% {
      transform: translateX(0);
    }

    50% {
      transform: translateX(-${showSuggestButton ? 100 : 300}px);
    }

    100% {
      transform: translateX(0);
    }
  }
`;
