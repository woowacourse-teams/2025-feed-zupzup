import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const headerContainer = css`
  display: flex;
  justify-content: space-between;
`;

export const headerText = css`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const headerCheerButton = css`
  display: flex;
  justify-content: flex-start;
`;

export const panelCaption = (theme: Theme) => css`
  margin-bottom: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.pretendard.caption}
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
  height: 40px;
`;

export const titleText = (theme: Theme) => css`
  ${theme.typography.bmHannaPro.bodyRegular};

  font-weight: 900;
`;
