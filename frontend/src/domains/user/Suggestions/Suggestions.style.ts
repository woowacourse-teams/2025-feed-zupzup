import { css } from '@emotion/react';
import { HEADER_HEIGHT_PX } from '@/constants';

export const suggestionLayout = css`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: calc(100vh - ${HEADER_HEIGHT_PX}px);
  margin-top: ${HEADER_HEIGHT_PX}px;
  overflow-y: auto;
`;

export const buttonContainer = css`
  display: flex;
  gap: 12px;
  width: 100%;
`;
