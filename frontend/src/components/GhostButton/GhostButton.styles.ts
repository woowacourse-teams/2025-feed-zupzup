import { css } from '@emotion/react';
import { colors, theme } from '@/theme';

export const ghostButton = css`
  ${theme.typography.inter.caption};
  background-color: ${colors.black[100]}33;
  border: 1px solid ${colors.black[100]}33;
  border-radius: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  color: ${colors.white[100]};
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.8;
  }

  &:active {
    opacity: 0.6;
  }
`;
