import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = css`
  position: relative;
  width: 100%;
  max-width: 384px;
  height: 100%;
  margin: 0 auto;
  padding: 16px;
`;

export const formContainer = css`
  position: relative;
  width: 100%;
  height: 317px;
`;

export const userInfo = (theme: Theme) => css`
  position: absolute;
  top: 24px;
  left: 79px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100px;
  height: 45px;
  color: ${theme.colors.gray[600]};
  transform: translateY(-50%);
  ${theme.typography.inter.captionSmallBold}

  p {
    line-height: 26px;
  }
`;

export const randomButton = (theme: Theme) => css`
  position: absolute;
  top: 8px;
  right: 60px;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 82px;
  height: 34px;

  ${theme.typography.inter.captionSmallBold}

  color: ${theme.colors.gray[600]};
  background-color: ${theme.colors.gray[100]};
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: #e0e0e0;
  }

  &:active {
    background-color: #d5d5d5;
  }
`;

export const avatar = css`
  position: absolute;
  top: 0;
  left: 10px;
  width: 45px;
  height: 45px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
`;

export const topInputBorder = (theme: Theme) => css`
  position: absolute;
  top: 0;
  right: 50px;
  left: 62px;
  height: 50px;
  border-radius: 10px;

  &::after {
    content: '';
    position: absolute;
    border: 2px solid ${theme.colors.gray[100]};
    inset: 0;
    pointer-events: none;
    border-radius: 10px;
  }
`;

export const lockButton = (theme: Theme) => css`
  position: absolute;
  top: 13px;
  right: 20px;
  z-index: 10;
  width: 16px;
  height: 16px;
  padding: 0;
  color: ${theme.colors.gray[500]};
  background: none;
  border: none;
  cursor: pointer;
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.7;
  }
`;

export const textareaContainer = (theme: Theme) => css`
  position: absolute;
  top: 61px;
  right: 11px;
  left: 11px;
  height: 224px;
  border-radius: 10px;

  &::after {
    content: '';
    position: absolute;
    border: 2px solid ${theme.colors.gray[100]};
    inset: 0;
    pointer-events: none;
    border-radius: 10px;
  }
`;

export const textarea = (theme: Theme) => css`
  position: absolute;
  inset: 8px;
  color: ${theme.colors.brown[300]};
  background: transparent;
  resize: none;
  outline: none;
  ${theme.typography.inter.captionSmall}

  border: none;

  &::placeholder {
    color: ${theme.colors.gray[500]};
  }
`;
