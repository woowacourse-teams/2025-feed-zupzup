import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const container = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  padding: 10px;
  background-color: #fbfffd;
  border-left: 2px solid ${theme.colors.green[200]};
  white-space: pre-wrap;
  word-break: keep-all;
  overflow-wrap: break-word;
`;

export const title = (theme: Theme) => css`
  font-weight: bold;
  color: ${theme.colors.green[200]};
`;
