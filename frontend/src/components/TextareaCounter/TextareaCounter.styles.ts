import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const textareaCounter = (theme: Theme) => css`
  position: absolute;
  right: 16px;
  bottom: 8px;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.captionSmall}

  pointer-events: none;
`;
