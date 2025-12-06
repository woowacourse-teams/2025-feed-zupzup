import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const ghostButton = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall};

  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  color: ${theme.colors.white[100]};
  background-color: ${theme.colors.white[100]}33;
  border: 1px solid ${theme.colors.white[100]}33;
  border-radius: 20px;
  cursor: pointer;
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.8;
  }

  &:active {
    opacity: 0.6;
  }
`;
