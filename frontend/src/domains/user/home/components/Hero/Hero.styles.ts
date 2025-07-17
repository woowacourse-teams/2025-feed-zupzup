import { css } from '@emotion/react';
import heroBackground from '@/assets/images/hero-background.png';
import { colors } from '@/theme';

export const heroStyle = css`
  background-image:
    linear-gradient(${colors.black[100]}59, ${colors.black[100]}59),
    url(${heroBackground});
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  text-align: center;
  margin: -14px -14px 0 -14px;
`;
