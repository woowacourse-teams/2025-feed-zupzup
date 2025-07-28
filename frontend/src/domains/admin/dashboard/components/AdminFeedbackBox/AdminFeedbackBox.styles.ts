import { Theme } from '@/theme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: FeedbackStatusType) => css`
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
  min-height: 100px;
  padding: 18px;
  border-radius: 14px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  ${type === 'CONFIRMED' && `background-color: ${theme.colors.gray[100]};`}
`;

export const topContainer = css`
  display: flex;
  justify-content: space-between;
`;

export const iconWrap = css`
  display: flex;
  gap: 14px;
`;

export const textWrap = css`
  display: flex;
  align-items: center;
  gap: 4px;
`;
