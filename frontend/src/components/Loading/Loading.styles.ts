import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const loadingContainer = css`
  height: 100%;
`;

export const loadingImageContainer = css`
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 20px;
`;

export const spinner = css`
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
`;

export const loadingText = (theme: Theme) => css`
  ${theme.typography.pretendard.caption}
`;
