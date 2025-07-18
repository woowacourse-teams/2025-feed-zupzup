import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const wrapper = css`
  display: flex;
  flex-direction: column;
  gap: 6px;
`;

export const header = (theme: Theme) => css`
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 10px;
  color: ${theme.colors.gray[600]};
`;

export const imageStyle = css`
  display: block;
  width: 100%;
  height: auto;
  max-height: 400px;
  border-radius: 12px;
  object-fit: cover;
`;
