import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const wrapperStyle = css`
  width: 100%;
`;

export const trackStyle = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 12px;
  background-color: ${theme.colors.white[100]}33;
  border-radius: 999px;
`;

export const fillStyle = (clamped: number) => css`
  width: ${clamped}%;
  height: 100%;
  background-color: white;
  border-radius: 999px;
`;

export const overlayStyle = css`
  border-radius: 999px;
`;
