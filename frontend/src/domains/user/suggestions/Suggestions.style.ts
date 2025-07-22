import { css } from '@emotion/react';
import { HEADER_HEIGHT_PX, PAGE_PADDING_PX } from '@/constants';

export const suggestionLayout = css`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: calc(100vh - ${HEADER_HEIGHT_PX});
  margin-top: calc(${PAGE_PADDING_PX}px + ${PAGE_PADDING_PX}px);
`;

export const buttonContainer = css`
  display: flex;
  gap: 12px;
  width: 100%;
`;
