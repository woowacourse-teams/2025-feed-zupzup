import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = css`
  display: flex;
  flex-direction: column;
  gap: 36px;
  width: 100%;
  max-width: 400px;
`;

export const headerContainer = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: center;
`;

export const headerTitle = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.small}
`;

export const headerSubtitle = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.caption}

  color: ${theme.colors.gray[400]}
`;

export const contentContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 10px;

  p {
    color: ${theme.colors.gray[600]};
  }
`;

export const contentTextarea = (theme: Theme) => css`
  width: 100%;
  height: 120px;
  padding: 12px;
  background-color: ${theme.colors.gray[100]}aa;
  border: 1px solid ${theme.colors.gray[100]};
  border-radius: 10px;
  resize: none;

  &::placeholder {
    color: ${theme.colors.gray[600]};
  }
`;

export const buttonContainer = (theme: Theme) => css`
  display: flex;
  justify-content: space-between;

  ${theme.typography.BMHANNAPro.caption}
`;

export const textareaContainer = css`
  position: relative;
`;
