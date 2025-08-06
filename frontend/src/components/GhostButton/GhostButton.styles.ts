import { css } from '@emotion/react';
import { colors } from '@/theme';
import { Theme } from '@/theme';

export const ghostButton = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};

  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  color: ${colors.white[100]};
  background-color: ${colors.white[100]}33;
  border: 1px solid ${colors.white[100]}33;
  border-radius: 16px;
  cursor: pointer;
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.8;
  }

  &:active {
    opacity: 0.6;
  }
`;
