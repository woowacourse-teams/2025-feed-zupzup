import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const secretText = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: ${theme.colors.gray[300]};

  ${theme.typography.pretendard.small}
`;

export const HighlightMyFeedback = (theme: Theme) => css`
  background-color: ${theme.colors.purple[100]};
`;
