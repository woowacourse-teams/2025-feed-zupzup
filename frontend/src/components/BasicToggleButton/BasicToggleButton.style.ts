import { Theme } from '@/theme';
import { css } from '@emotion/react';

export const toggleWrapper = css`
  position: relative;
  width: 50px;
  height: 24px;
  cursor: pointer;
`;

export const trackStyle = (theme: Theme, isToggled: boolean) => css`
  width: 100%;
  height: 100%;
  background-color: ${isToggled
    ? theme.colors.purple[100]
    : 'rgb(224, 224, 224)'};
  border-radius: 30px;
  transition: background-color 0.3s;
`;

export const circleStyle = (isToggled: boolean) => css`
  position: absolute;
  top: 2px;
  left: ${isToggled ? '27px' : '1px'};
  width: 20px;
  height: 20px;
  background-color: #fffefe;
  border-radius: 50%;
  box-shadow: 0 0 2px rgb(0 0 0 / 30%);
  transition: left 0.3s;
`;
