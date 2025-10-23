import { css } from '@emotion/react';

export const categoryItemContainer = (isSelected: boolean) => css`
  display: flex;
  gap: 16px;
  width: 100%;
  padding: 15px;
  background-color: ${isSelected ? 'rgba(115,86,255,0.1)' : 'white'};
  border-radius: 8px;
  cursor: pointer;
`;

export const iconStyle = css`
  font-size: 14px;
`;

export const categoryStyle = css`
  font-size: 14px;
`;
