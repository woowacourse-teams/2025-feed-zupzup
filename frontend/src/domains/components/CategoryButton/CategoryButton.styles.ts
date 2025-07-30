import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  width: 48%;
  padding: 16px;
  text-align: center;
  border: 1px solid ${theme.colors.gray[200]};
  border-radius: 24px;
  transition: all 0.1s ease;

  ${theme.typography.inter.caption}

  &:active {
    border: 1px solid ${theme.colors.purple[100]};
    transform: scale(0.97);
  }
`;

export const iconCSS = css`
  font-size: 36px;
  text-align: center;
`;
