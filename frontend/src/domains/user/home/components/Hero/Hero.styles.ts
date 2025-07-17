import { css } from '@emotion/react';
import heroBackground from '@/assets/images/hero-background.png';
import { colors, theme } from '@/theme';

export const hero = css`
  background-image:
    linear-gradient(${colors.black[100]}59, ${colors.black[100]}59),
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

export const heroHeader = css`
  width: 100%;
  height: 56px;
  background-color: ${colors.black[100]}33;
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
  margin: 0 6px 20px 6px;
  width: 100%;
  padding: 0 20px;
`;

export const heroHr = css`
  width: 56px;
  height: 2px;
  background-color: ${colors.yellow[200]};
  border: none;
  margin: 0 0 14px 0;
`;

export const heroTitle = css`
  ${theme.typography.inter.small};
  color: ${colors.yellow[200]};
  margin: 0 0 14px 0;
`;

export const heroLogo = css`
  width: 112px;
  height: auto;
  margin: 0 0 16px 0;
`;

export const heroDescription = css`
  ${theme.typography.inter.small};
  color: ${colors.white[100]};
  margin: 0 0 24px 0;
  text-align: left;
  line-height: 1.5;
`;
