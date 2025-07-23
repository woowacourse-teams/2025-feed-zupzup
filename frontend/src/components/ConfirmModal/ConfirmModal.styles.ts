import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const content = css`
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  gap: 16px;
`;

export const title = (theme: Theme) => css`
  ${theme.typography.inter.h4};

  color: ${theme.colors.black[100]};
`;

export const message = (theme: Theme) => css`
  ${theme.typography.inter.bodyRegular};

  color: ${theme.colors.gray[600]};
`;

export const buttonContainer = css`
  display: flex;
  justify-content: center;
  gap: 12px;
`;
