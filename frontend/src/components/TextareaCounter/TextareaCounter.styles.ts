import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const textareaCounter = (theme: Theme, inset: string) => css`
  position: absolute;
  inset: ${inset};
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.captionSmall}

  pointer-events: none;
`;
