import { css } from '@emotion/react';

export const container = css`
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
`;

export const slideContainer = css`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
`;

export const paginationContainer = css`
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 24px 0;
`;

export const paginationDot = css`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: background-color 0.3s ease;
`;

export const contentContainer = css`
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 80px;
`;
