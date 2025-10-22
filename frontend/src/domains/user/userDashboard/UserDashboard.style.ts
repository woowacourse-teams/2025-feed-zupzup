import { Theme } from '@/theme';
import { css, keyframes } from '@emotion/react';

export const dashboardLayout = css`
  display: flex;
  flex-direction: column;
  gap: 24px;
`;

export const goOnboardButton = (theme: Theme) => css`
  width: 60px;
  height: 60px;
  background-color: ${theme.colors.purple[100]};

  &:hover {
    background-color: ${theme.colors.purple[100]}aa;
  }
`;

export const goTopButton = (theme: Theme) => css`
  width: 40px;
  height: 40px;
  background-color: white;
  border: 1px solid ${theme.colors.purple[100]};
`;

export const highlightFade = keyframes`
  0% {
    background-color: #f0ede6;
  }
  100% {
    background-color: white;
  }
`;

export const highlightStyle = css`
  border-radius: 12px;
  animation: ${highlightFade} 2s linear;
`;

export const myFeedbackStyle = (theme: Theme, isMyFeedback: boolean) => css`
  border: 1.5px solid
    ${isMyFeedback ? theme.colors.green[100] : theme.colors.white[100]};
`;
