import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const dashboardLayout = css`
  display: flex;
  flex-direction: column;
  gap: 24px;
`;

export const titleText = (theme: Theme) => css`
  ${theme.typography.bmHannaPro.bodyRegular};

  font-weight: 900;
`;
