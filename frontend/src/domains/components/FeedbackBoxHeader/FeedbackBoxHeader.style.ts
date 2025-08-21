import { Theme } from '@/theme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { css } from '@emotion/react';

export const imgContainer = css`
  display: flex;
  align-items: center;
  gap: 14px;
`;

export const imgLayout = css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30px;
  height: 30px;
`;

export const userNameStyle = (theme: Theme, type: FeedbackStatusType) => css`
  ${theme.typography.BMHANNAPro.caption}

  line-height: 20px;

  ${type === 'WAITING'
    ? `color : ${theme.colors.black}`
    : `color : ${theme.colors.gray[300]}`}
`;

export const userCategoryStyle = (
  theme: Theme,
  type: FeedbackStatusType
) => css`
  ${theme.typography.pretendard.captionSmall}

  line-height: 20px;

  ${type === 'WAITING'
    ? `color : ${theme.colors.gray[500]}`
    : `color : ${theme.colors.gray[300]}`}
`;

export const headerInfoBox = css`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
`;
