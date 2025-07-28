import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const totalBar = css`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 8px;
  background-color: inherit;
  overflow: hidden;
`;

export const currentBar = (percent: number, theme: Theme) => css`
  width: ${percent}%;
  height: 100%;
  background-color: ${theme.colors.yellow[200]};
  transition: width 0.3s ease;
`;
