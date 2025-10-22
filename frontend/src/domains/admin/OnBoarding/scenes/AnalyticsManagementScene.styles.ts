import { css } from '@emotion/react';

export const container = css`
  position: relative;
  width: 85%;
  max-width: 350px;
  aspect-ratio: 4/3;
  margin: 0 auto;
`;

export const background = css`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  border-radius: 24px;
  background-color: #7356ff08;
`;

export const mainChartContainer = css`
  position: absolute;
  top: 16px;
  right: 16px;
  bottom: 16px;
  left: 16px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background-color: #7356ff;
`;

export const chartIconWrapper = css`
  margin-bottom: 12px;
`;

export const chartIcon = css`
  width: 32px;
  height: 32px;
  color: #ffffff;
`;

export const animatedBarsContainer = css`
  display: flex;
  align-items: flex-end;
  gap: 4px;
  margin-bottom: 12px;
`;

export const animatedBar = css`
  width: 8px;
  border-radius: 2px;
  background-color: #ffffff;
`;

export const statsDisplay = css`
  display: flex;
  align-items: center;
  gap: 12px;
`;

export const statsIcon = css`
  width: 16px;
  height: 16px;
  color: #ffffff;
`;

export const statsText = css`
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
`;

export const floatingDataPoint = css`
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
`;
