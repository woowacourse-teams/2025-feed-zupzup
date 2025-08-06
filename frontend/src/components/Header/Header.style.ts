import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const header = css`
  position: sticky;
  top: -${PAGE_PADDING_PX}px;
  left: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: calc(100% + 2 * ${PAGE_PADDING_PX}px);
  margin: -${PAGE_PADDING_PX}px;
  padding: 20px;
  background-color: white;
`;

export const headerSection = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

export const headerTitle = (theme: Theme) => css`
  font-size: 14px;
  color: ${theme.colors.darkGray[400]};

  ${theme.typography.BMHANNAPro.bodyBold}
`;

export const headerSubtitle = (theme: Theme) => css`
  color: ${theme.colors.gray[600]};
  ${theme.typography.BMHANNAAir.caption}
`;

export const captionSection = css`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;
