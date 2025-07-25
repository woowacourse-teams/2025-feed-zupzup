import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const basicTextAreaContainer = css`
  position: relative;
`;

export const textArea = (theme: Theme) => css`
  width: 100%;
  height: 120px;
  padding: 20px 0 0 12px;
  background-color: white;
  border: 1px solid ${theme.colors.gray[200]};
  resize: none;
  border-radius: 16px;
  ${theme.typography.inter.caption}

  ::placeholder {
    ${theme.typography.inter.caption}

    color : ${theme.colors.gray[500]};
  }
`;

export const caption = (theme: Theme) => css`
  position: absolute;
  right: 20px;
  bottom: 20%;
  color: ${theme.colors.gray[500]};

  ${theme.typography.inter.caption}
`;
