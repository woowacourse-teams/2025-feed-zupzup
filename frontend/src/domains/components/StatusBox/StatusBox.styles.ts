import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = (
  theme: Theme,
  width: number | string,
  height: number | string
) => css`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 20px;
  width: ${typeof width === 'number' ? `${width}px` : width};
  height: ${typeof height === 'number' ? `${height}px` : height};
  background-color: ${theme.colors.white[100]};
  border-radius: 16px;
`;

export const textIconContainer = (theme: Theme) => css`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  width: 70px;
  height: 70px;
  font-size: 30px;
  font-weight: 700;
  background-color: ${theme.colors.gray[100]};
  border-radius: 50%;
`;

export const titleContainer = (theme: Theme) => css`
  font-size: 20px;
  font-weight: 700;
  ${theme.typography.inter.bodyRegular}

  color: ${theme.colors.black[100]};
`;

export const descriptionContainer = (theme: Theme) => css`
  font-size: 12px;
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[500]};
`;
