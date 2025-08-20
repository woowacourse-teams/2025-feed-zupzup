import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = css`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
`;

export const content = css`
  text-align: center;
  max-width: 400px;
`;

export const errorCode = (theme: Theme) => css`
  font-size: 120px;
  font-weight: 700;
  color: ${theme.colors.purple[100]};
  margin-bottom: 20px;
`;

export const title = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold}
  color: ${theme.colors.darkGray[200]};
  margin-bottom: 16px;
`;

export const description = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}
  color: ${theme.colors.gray[400]};
  margin-bottom: 32px;
`;

export const button = css`
  min-width: 200px;
`;
