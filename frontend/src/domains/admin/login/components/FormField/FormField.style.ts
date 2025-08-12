import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const loginForm = css`
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
`;

export const fieldLabel = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};

  margin-bottom: 4px;
`;

export const inputFormField = (theme: Theme, isValid: boolean) => css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  padding: 12px 16px;
  color: ${theme.colors.black[100]};
  background-color: ${theme.colors.white[300]};
  border: 1px solid ${isValid ? theme.colors.gray[200] : theme.colors.red[100]};
  border-radius: 16px;

  ${theme.typography.pretendard.captionSmall};

  &::placeholder {
    color: ${theme.colors.gray[500]};
    ${theme.typography.pretendard.captionSmall};
  }
`;

export const errorMessageStyle = (theme: Theme) => css`
  height: 14px;
  margin-top: 4px;
  color: ${theme.colors.red[100]};

  ${theme.typography.pretendard.captionSmall};
`;
