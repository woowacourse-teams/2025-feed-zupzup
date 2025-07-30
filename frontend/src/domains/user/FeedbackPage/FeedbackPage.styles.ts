import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const skipText = (theme: Theme) => css`
  display: flex;
  align-items: center;
  font-weight: bold;
  color: ${theme.colors.darkGray[100]};
`;

export const buttonGroupContainer = css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: auto;
  padding-bottom: 70px;
`;
export const submitButtonContainer = css`
  display: flex;
  justify-content: center;
`;

export const skipButtonContainer = css`
  display: flex;
  justify-content: center;
`;

export const contentContainer = css`
  margin-top: 50px;
  margin-bottom: 30px;
`;

export const container = css`
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  min-height: 500px;
  text-align: center;
`;

export const mainContent = css`
  display: flex;
  flex: 1; /* 남은 공간 모두 차지 */
  flex-direction: column;
`;

export const arrowLeftIconContainer = css`
  position: absolute;
  top: 0;
  left: 0;
`;

export const titleContainer = css`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin-bottom: 70px;
`;

export const title = (theme: Theme) => css`
  ${theme.typography.inter.h5}
`;

export const mainTitle = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.small}

  font-size: 32px;
  color: ${theme.colors.purple[100]};
`;

export const subTitle = (theme: Theme) => css`
  ${theme.typography.inter.caption}

  line-height: 1.5;
  color: ${theme.colors.darkGray[100]};
`;

export const questionTitle = (theme: Theme) => css`
  ${theme.typography.inter.smallBold}

  margin-top: 50px;
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
  margin-bottom: 50px;
  padding: 24px 0;
  text-align: center;
  background-color: ${theme.colors.white[300]};
  border-radius: 16px;
`;
