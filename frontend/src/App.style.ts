import { HEADER_HEIGHT_PX } from './constants';
import { css } from '@emotion/react';

export const appContainer = (hasHeader: boolean) => css`
  box-sizing: border-box;
  height: 100%;
  padding-top: ${hasHeader ? `${HEADER_HEIGHT_PX}px` : '0'};
`;

export const main = css`
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px 0;
`;
