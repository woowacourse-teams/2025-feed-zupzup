import { css } from '@emotion/react';

export const container = css`
  position: relative;
  width: 85%;
  max-width: 350px;
  aspect-ratio: 4/3;
  margin: 0 auto;
`;

export const backgroundLayer = css`
  position: absolute;
  top: 8px;
  right: 8px;
  bottom: 8px;
  left: 8px;
  border-radius: 24px;
`;

export const mainCard = css`
  position: absolute;
  top: 8px;
  right: 8px;
  bottom: 8px;
  left: 8px;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background-color: #7356ff;
`;

export const iconWrapper = css`
  color: #ffffff;
  margin-bottom: 16px;
`;

export const mainIcon = css`
  width: 40px;
  height: 40px;
`;

export const uiElementsContainer = css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
`;

export const uiElement = css`
  height: 8px;
  border-radius: 50px;
  background-color: rgba(255, 255, 255, 0.3);
`;

export const starRatingContainer = css`
  display: flex;
  gap: 4px;
  margin-top: 16px;
`;

export const starIcon = css`
  width: 12px;
  height: 12px;
  color: #ffffff;
  fill: #ffffff;
`;
