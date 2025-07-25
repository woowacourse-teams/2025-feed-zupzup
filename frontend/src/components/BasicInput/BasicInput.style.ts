import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const basicInputContainer = css`
  position: relative;
`;

export const basicInput = (theme: Theme) => css`
  width: 100%;
  height: 60px;
  padding: 12px;
  background-color: white;
  border: 1px solid ${theme.colors.gray[200]};
  border-radius: 16px;
  ${theme.typography.inter.caption}

  ::placeholder {
    ${theme.typography.inter.caption}

    color : ${theme.colors.gray[500]};
  }
`;

export const caption = (theme: Theme) => css`
  position: absolute;
  top: 40%;
  right: 20px;
  color: ${theme.colors.gray[500]};

  ${theme.typography.inter.caption}
`;
