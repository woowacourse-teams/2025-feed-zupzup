import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const feedbackRoomContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: start;
  gap: 16px;
  padding: 24px;
  color: ${theme.colors.darkGray[100]}bb;
  background-color: ${theme.colors.white[400]};
  border-radius: 16px;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: ${theme.colors.gray[100]};
  }
`;

export const feedbackTitleContainer = css`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
`;

export const feedbackRoomTitle = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  line-height: 1;
  color: ${theme.colors.darkGray[100]};

  ${theme.typography.pretendard.smallBold};
`;

export const dot = (theme: Theme) => css`
  flex-shrink: 0;
  width: 12px;
  height: 12px;
  margin-bottom: 4px;
  background-color: ${theme.colors.purple[100]};
  border-radius: 50%;
`;

export const pendingText = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};

  & strong {
    color: ${theme.colors.purple[100]};
    ${theme.typography.pretendard.h1};
  }
`;
