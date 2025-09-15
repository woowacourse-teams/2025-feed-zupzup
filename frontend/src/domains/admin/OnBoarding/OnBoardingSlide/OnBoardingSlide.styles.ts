import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 48px 32px;
  min-height: 500px;
`;

export const contentContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 80px;
`;

export const titleBadge = (theme: Theme) => css`
  display: inline-block;
  padding: 8px 16px;
  background-color: ${theme.colors.gray[100]};
  border-radius: 8px;
`;

export const titleText = (theme: Theme) => css`
  font-size: 14px;
  color: ${theme.colors.gray[600]};
  font-weight: 500;
  ${theme.typography.pretendard.captionSmall}
`;

export const subtitleText = (theme: Theme) => css`
  font-size: 20px;
  font-weight: 700;
  line-height: 1.6;
  max-width: 384px;
  margin: 0 auto;
  color: ${theme.colors.black[100]};
  ${theme.typography.pretendard.bodyBold}
`;

export const iconContainer = css`
  display: flex;
  align-items: center;
  justify-content: center;
`;
