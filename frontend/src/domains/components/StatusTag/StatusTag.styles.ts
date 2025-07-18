import { css } from '@emotion/react';
import { Theme } from '@/theme';
import { Type } from '@/types/feedbackStatus.types';

export const content = css`
  display: flex;
  align-items: center;
  gap: 4px;
`;

export const container = (theme: Theme, type: Type) => css`
  color: ${type === 'complete' && theme.colors.white[100]};
  background-color: ${type === 'incomplete'
    ? theme.colors.yellow[100]
    : theme.colors.green[100]};
  ${type === 'incomplete' && `border: 1px solid ${theme.colors.yellow[200]};`}
`;
