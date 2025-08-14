import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const headerLayout = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  width: 100%;
  background-color: ${theme.colors.blue[100]};
`;

export const homeCaptionContainer = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  margin-top: 16px;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption};

  strong {
    color: ${theme.colors.purple[100]};
    cursor: pointer;
  }
`;

export const homeTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold};

  color: ${theme.colors.white[100]};
`;

export const homeTag = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall};

  padding: 6px 12px;
  color: ${theme.colors.white[100]};
  background-color: ${theme.colors.white[100]}33;
`;

export const feedbackProgressStatus = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 20px;
  color: ${theme.colors.gray[500]};
  background-color: ${theme.colors.white[100]}22;
  border-radius: 12px;

  ${theme.typography.pretendard.captionSmall};
`;

export const progressBar = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 8px;
  background-color: ${theme.colors.gray[200]};
  border-radius: 4px;
  overflow: hidden;
`;

export const progressStatus = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  color: ${theme.colors.gray[200]};

  > p:first-of-type {
    ${theme.typography.pretendard.captionBold};
  }
`;

export const progressStatusSummary = (theme: Theme) => css`
  display: flex;
  justify-content: start;
  align-items: center;
  width: 100%;
  color: ${theme.colors.gray[300]};
  border-radius: 4px;

  ${theme.typography.pretendard.captionSmall};
`;
