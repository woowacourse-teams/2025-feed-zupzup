import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const panelCaption = (theme: Theme) => css`
  margin-bottom: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.inter.caption}
`;

export const panelLayout = css`
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, 1fr);
`;

export const cheerButtonLayout = css`
  display: flex;
  justify-content: flex-end;
  width: 100%;
  margin: 32px 0;
`;

export const titleText = (theme: Theme) => css`
  ${theme.typography.bmHannaPro.bodyRegular};

  font-weight: 900;
`;
