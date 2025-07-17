import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const textArea = (theme: Theme) => css`
  width: 100%;
  height: 96px;
  padding: 12px;
  background-color: white;
  border: 1px solid #d9d9d9;
  resize: none;
  border-radius: 16px;

  ::placeholder {
    ${theme.typography.inter.small}

    color : ${theme.colors.gray[500]};
  }
`;
