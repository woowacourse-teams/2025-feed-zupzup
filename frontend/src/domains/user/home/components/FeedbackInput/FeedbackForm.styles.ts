import { css } from '@emotion/react';
import { Theme } from '@/theme';

export const container = css`
  position: relative;
  width: 100%;
  margin: 0 auto;
`;

export const formContainer = css`
  position: relative;
  width: 100%;
  height: 317px;
`;

export const userInfo = (theme: Theme) => css`
  position: absolute;
  top: 24px;
  left: 34px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  width: calc(100% - 124px);
  height: 45px;
  color: ${theme.colors.gray[600]};
  transform: translateY(-50%);
  ${theme.typography.inter.caption}

  p {
    line-height: 26px;
  }
`;

export const randomButton = (theme: Theme) => css`
  position: absolute;
  top: 8px;
  right: 16px;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 82px;
  height: 34px;

  ${theme.typography.inter.caption}

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

export const topInputBorder = (theme: Theme) => css`
  position: absolute;
  top: 0;
  right: 10px;
  left: 10px;
  height: 50px;
  border-radius: 10px;
  pointer-events: none;

  &::after {
    content: '';
    position: absolute;
    border: 2px solid ${theme.colors.gray[100]};
    inset: 0;
    pointer-events: none;
    border-radius: 10px;
  }
`;

export const textareaContainer = (theme: Theme) => css`
  position: absolute;
  top: 61px;
  right: 11px;
  left: 11px;
  height: 150px;
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
  padding: 10px 16px;
  inset: 8px;
  background: transparent;
  resize: none;
  outline: none;

  ${theme.typography.inter.caption}

  border: none;
`;

export const toggleButtonContainer = css`
  position: absolute;
  top: 226px;
  left: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
`;

export const toggleButtonText = (theme: Theme) => css`
  ${theme.typography.inter.captionSmall}

  margin-left: 10px;
  color: ${theme.colors.gray[600]};
`;

export const usernameInput = (theme: Theme) => css`
  width: 100%;
  font-size: inherit;
  font-weight: inherit;
  text-align: start;
  color: black;
  background: transparent;
  border: none;
  outline: none;

  &:disabled {
    color: ${theme.colors.gray[500]};
    cursor: not-allowed;
  }
`;
