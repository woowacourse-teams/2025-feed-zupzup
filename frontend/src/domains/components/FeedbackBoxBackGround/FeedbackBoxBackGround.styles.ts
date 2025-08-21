import { Theme } from '@/theme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: FeedbackStatusType) => css`
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
  min-height: 100px;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  ${type === 'WAITING'
    ? `background-color: ${theme.colors.white[100]};`
    : `background-color: ${theme.colors.green[100]}22;`}
`;
