import { Theme } from '@/theme';
import { StatusType } from './StatusTag';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: StatusType) => css`
  color: ${type === 'complete' && theme.colors.white[100]};
  background-color: ${type === 'incomplete'
    ? theme.colors.yellow[100]
    : theme.colors.green[100]};
  ${type === 'incomplete' && `border: 1px solid ${theme.colors.yellow[200]};`}
`;
