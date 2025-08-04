import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const modalContainer = css`
  border-radius: 30px;
`;

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
  margin-top: 10%;
`;

export const completeContainer = css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
`;

export const spinner = (theme: Theme) => css`
  width: 40px;
  height: 40px;
  border: 3px solid ${theme.colors.gray[100]};
  border-top: 3px solid ${theme.colors.purple[100]};
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

export const checkIcon = (theme: Theme) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 70px;
  height: 70px;
  font-size: 30px;
  font-weight: bold;
  color: white;
  background-color: ${theme.colors.gray[100]};
  border-radius: 50%;
`;

export const errorIcon = (theme: Theme) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 70px;
  height: 70px;
  font-size: 30px;
  font-weight: bold;
  color: white;
  background-color: ${theme.colors.gray[100]};
  border-radius: 50%;
`;

export const messageText = (theme: Theme) => css`
  ${theme.typography.inter.bodyRegular};

  margin: 0;
  color: ${theme.colors.black[100]};
`;
