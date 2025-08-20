import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const QRText = (theme: Theme) => css`
  ${theme.typography.pretendard.captionSmall}

  font-size: 12px;
  color: ${theme.colors.darkGray[100]};
`;
