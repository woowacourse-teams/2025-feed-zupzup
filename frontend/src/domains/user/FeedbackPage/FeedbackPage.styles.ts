import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const skipText = (theme: Theme) => css`
  display: flex;
  align-items: center;
  font-weight: bold;
  color: ${theme.colors.darkGray[100]};
`;

export const skipButtonContainer = css`
  position: absolute;
  top: 590px;
  right: 0;
  left: 0;
  display: flex;
  justify-content: center;
`;

export const contentContainer = css`
  margin-top: 50px;
  margin-bottom: 30px;
`;

export const container = css`
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 500px; /* 최소 높이 설정 */
  text-align: center;
  overflow: hidden; /* 스크롤 방지 */
`;

export const titleContainer = css`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin-bottom: 16px;
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
