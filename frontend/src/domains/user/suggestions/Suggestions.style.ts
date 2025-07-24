import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const suggestionLayout = css`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 44px;
  width: 100%;
  height: calc(100vh);
  margin-top: calc(${PAGE_PADDING_PX}px * 3);
`;

export const buttonContainer = css`
  display: flex;
  gap: 12px;
  width: 100%;
`;

export const formContainer = css`
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
`;

export const bannerDescription = (theme: Theme) => css`
  display: flex;
  align-items: center;
  white-space: pre-line;
  line-height: 1.2;

  color: ${theme.colors.darkGray[100]};
  ${theme.typography.BMHANNAPro.small}
`;

export const bannerImage = css`
  width: 20%;
`;
