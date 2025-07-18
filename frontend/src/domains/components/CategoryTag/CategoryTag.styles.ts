import { Theme } from '@/theme';
import { Type } from '@/types/feedbackStatus.types';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: Type) => css`
  background-color: ${type === 'incomplete'
    ? theme.colors.yellow[200]
    : theme.colors.gray[200]};
  border: 1px solid
    ${type === 'incomplete' ? theme.colors.yellow[200] : theme.colors.gray[200]};
`;
