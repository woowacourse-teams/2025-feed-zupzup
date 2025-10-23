import { css } from '@emotion/react';

export const container = css`
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
  min-height: 50px;

  @media (min-height: 700px) {
    min-height: 350px;
  }

  @media (min-width: 499px) {
    min-height: 480px;
  }

  @media (max-height: 699px) {
    min-height: 40px;
  }

  @media (max-height: 500px) {
    min-height: 30px;
  }

  @media (max-height: 400px) {
    min-height: 20px;
  }
`;

export const slideContainer = css`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
`;

export const paginationContainer = css`
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 24px 0;
`;

export const paginationDot = css`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: background-color 0.3s ease;
`;

export const contentContainer = css`
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 40px;

  @media (max-height: 700px) {
    gap: 8px;
    margin-bottom: 20px;
  }

  @media (max-height: 500px) {
    gap: 4px;
    margin-bottom: 10px;
  }

  @media (max-height: 400px) {
    gap: 2px;
    margin-bottom: 5px;
  }

  @media (max-height: 350px) {
    gap: 1px;
    margin-bottom: 2px;
  }
`;
