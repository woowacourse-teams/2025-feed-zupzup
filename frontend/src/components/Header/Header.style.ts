import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const header = (theme: Theme) => css`
  position: fixed;
  top: 0;
  left: 50%;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: ${theme.colors.blue[100]};
  transform: translateX(-50%);
`;
export const arrowTitleContainer = css`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 16px;
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
