import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const modalContent = css`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 32px 24px;
  text-align: center;
`;

export const loadingContainer = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
`;

export const completeContainer = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
`;

export const spinner = css`
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #007bff;
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

export const checkIcon = css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  font-size: 30px;
  font-weight: bold;
  color: white;
  background-color: #28a745;
  border-radius: 50%;
`;

export const messageText = (theme: Theme) => css`
  ${theme.typography.inter.bodyRegular};

  margin: 0;
  color: ${theme.colors.black[100]};
`;
