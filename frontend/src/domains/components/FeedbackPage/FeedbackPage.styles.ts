import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const container = css`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  max-height: 100vh;
  text-align: center;
`;

export const title = (theme: Theme) => css`
  ${theme.typography.inter.h5}

  margin-top: 10%;
  margin-bottom: 20px;
`;

export const subTitle = (theme: Theme) => css`
  ${theme.typography.inter.small}

  line-height: 1.5;
  color: ${theme.colors.darkGray[100]};
`;

export const place = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.small}

  font-size: 32px;
  color: ${theme.colors.purple[100]};
`;

export const questionTitle = (theme: Theme) => css`
  ${theme.typography.inter.smallBold}

  margin-top: 80px;
  margin-left: 10px;
  text-align: left;
`;

export const question = (theme: Theme) => css`
  ${theme.typography.inter.small}

  color: ${theme.colors.gray[600]};
`;

export const questionContainer = (theme: Theme) => css`
  position: relative;
  margin-top: 10px;
  margin-bottom: 80px;
  padding: 24px 0;
  text-align: center;
  background-color: ${theme.colors.white[300]};
  border-radius: 16px;
`;
