import { css } from '@emotion/react';

export const floatingButton = (inset: string[]) => css`
  position: sticky;
  inset: ${inset.join(' ')};
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
  border-radius: 50%;
  cursor: pointer;
  transition:
    bottom 0.3s ease,
    background-color 0.2s ease;
`;
