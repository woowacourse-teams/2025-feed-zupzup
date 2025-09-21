import { css, keyframes } from '@emotion/react';

export const adminOrganizationList = css`
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  min-height: calc(100% - 100px);
  margin-bottom: 20px;
`;

const spin = keyframes`
  to { transform: rotate(360deg); }
`;

export const loadingContainer = css`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 160px;
  font-size: 14px;
  color: #4b5563;
`;

export const adminSpinner = css`
  width: 24px;
  height: 24px;
  margin-right: 8px;
  border: 3px solid #d1d5db;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: ${spin} 1s linear infinite;
`;

export const emptyAdminOrganization = css`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
`;
