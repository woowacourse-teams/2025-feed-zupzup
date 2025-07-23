import { css } from '@emotion/react';
import { Theme } from '@/theme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

export const content = css`
  display: flex;
  align-items: center;
  gap: 4px;
`;

export const container = (theme: Theme, type: FeedbackStatusType) => css`
  color: ${type === 'CONFIRMED' && theme.colors.white[100]};
  background-color: ${type === 'WAITING'
    ? theme.colors.yellow[100]
    : theme.colors.green[100]};
  ${type === 'WAITING' && `border: 1px solid ${theme.colors.yellow[200]};`}
`;
