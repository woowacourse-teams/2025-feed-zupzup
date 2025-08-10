import { HEADER_HEIGHT_PX } from './constants';
import { css } from '@emotion/react';

export const appContainerStyle = (hasHeader: boolean) => css`
  padding-top: ${hasHeader ? `${HEADER_HEIGHT_PX}px` : '0'};
`;
