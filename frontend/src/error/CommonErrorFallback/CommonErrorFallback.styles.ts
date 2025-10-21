import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = css`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px;
`;

export const content = css`
  max-width: 400px;
  text-align: center;
  background: white;
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
`;

export const title = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold}
  color: ${theme.colors.darkGray[200]};
  margin-bottom: 20px;
`;

export const subtitle = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}
  color: ${theme.colors.gray[400]};
  margin-bottom: 20px;
`;

export const buttonContainer = css`
  display: flex;
  justify-content: center;
`;

export const reportSection = css`
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
`;

export const reportText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}
  color: ${theme.colors.gray[500]};
  margin-bottom: 12px;
`;

export const reportLink = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}
  color: ${theme.colors.purple[100]};
  text-decoration: none;
  font-weight: 500;

  &:hover {
    text-decoration: underline;
    color: ${theme.colors.purple[200]};
  }
`;
