import { Theme } from '@/theme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: FeedbackStatusType) => css`
  background-color: ${type === 'WAITING'
    ? theme.colors.yellow[200]
    : theme.colors.gray[200]};
  border: 1px solid
    ${type === 'WAITING' ? theme.colors.yellow[200] : theme.colors.gray[200]};
`;
