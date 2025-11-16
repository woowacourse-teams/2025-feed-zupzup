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

export const cheerButtonLayout = css`
  display: flex;
  justify-content: flex-end;
  width: 100%;
  height: 40px;
  gap: 8px;
`;

export const titleText = (theme: Theme) => css`
  ${theme.typography.pretendard.bodyBold};

  font-weight: 900;
`;

export const panelCaption = (theme: Theme) => css`
  margin-bottom: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.pretendard.caption}
`;
