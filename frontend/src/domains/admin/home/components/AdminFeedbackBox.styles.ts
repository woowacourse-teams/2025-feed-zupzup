import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const container = (theme: Theme, type: 'incomplete' | 'complete') => css`
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
  min-height: 100px;
  padding: 18px;
  border-radius: 14px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 10%);

  ${type === 'complete' && `background-color: ${theme.colors.gray[100]};`}
`;

export const topContainer = css`
  display: flex;
  justify-content: space-between;
`;

export const iconWrap = css`
  display: flex;
  gap: 14px;
`;
