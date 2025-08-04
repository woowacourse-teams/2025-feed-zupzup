import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const skipText = (theme: Theme) => css`
  display: flex;
  align-items: center;
  font-weight: bold;
  color: ${theme.colors.darkGray[100]};
`;

export const skipIcon = css`
  font-size: 20px;
`;

export const container = css`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  text-align: center;
`;

export const title = (theme: Theme) => css`
  ${theme.typography.inter.h5}

  margin-top: 8%;
`;

export const place = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.small}

  font-size: 32px;
  color: ${theme.colors.purple[100]};
`;

export const questionTitle = (theme: Theme) => css`
  ${theme.typography.inter.bodyBold}
`;

export const question = (theme: Theme) => css`
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[600]};
`;

export const questionContainer = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 8%;
  padding: 22px 0;
  text-align: center;
  background-color: ${theme.colors.white[300]};
  border-radius: 16px;
`;

export const buttonContainer = css`
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 10px;
  margin-top: 8%;
`;
