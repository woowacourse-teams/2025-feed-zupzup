import { Position } from '@/domains/components/FloatingButton/FloatingButton';
import { css } from '@emotion/react';

export const floatingButton = (inset: Position) => css`
  position: sticky;
  inset: ${inset.top || 'auto'} ${inset.right || 'auto'}
    ${inset.bottom || 'auto'} ${inset.left || 'auto'};
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
