import { PAGE_PADDING_PX } from '@/constants';
import { css } from '@emotion/react';

export const suggestionLayout = css`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: calc(100vh);
  margin-top: calc(${PAGE_PADDING_PX}px + ${PAGE_PADDING_PX}px);
`;

export const buttonContainer = css`
  display: flex;
  gap: 12px;
  width: 100%;
`;
