import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const header = (theme: Theme) => css`
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0;
  padding: 20px;
  background-color: ${theme.colors.blue[100]};
`;

export const headerSection = css`
  display: flex;
  align-items: center;
`;

export const headerTitle = (theme: Theme) => css`
  font-size: 14px;
  color: ${theme.colors.white[100]};
  ${theme.typography.BMHANNAPro.small}
`;

export const headerSubtitle = (theme: Theme) => css`
  color: ${theme.colors.gray[200]};
  ${theme.typography.pretendard.captionSmall}
`;

export const captionSection = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;
