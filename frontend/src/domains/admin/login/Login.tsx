import { PAGE_PADDING_PX } from '@/constants';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import { useState } from 'react';

export default function Login() {
  const theme = useAppTheme();

  const [name, setName] = useState('');
  const [password, setPassword] = useState('');

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setName(e.target.value);
  };
  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  return (
    <div css={login(theme)}>
      <div css={loginContainer}>
        <p css={loginText(theme)}>로그인</p>
        <p css={loginDescription(theme)}>게정에 로그인 하세요</p>
      </div>
      <form css={loginForm(theme)}>
        {/* Form Container */}

        {/* FormField */}
      </form>
    </div>
  );
}

export const login = (theme: Theme) => css`
  position: absolute;
  top: -${PAGE_PADDING_PX}px;
  left: -${PAGE_PADDING_PX}px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: calc(100% + ${PAGE_PADDING_PX}px);
  color: ${theme.colors.white[100]};
  background-color: ${theme.colors.blue[100]};
`;

export const loginContainer = () => css`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

export const loginText = (theme: Theme) => css`
  ${theme.typography.BMHANNAPro.small};
`;

export const loginDescription = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};
`;

export const loginForm = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  max-width: 400px;
  padding: ${PAGE_PADDING_PX}px;
  background-color: ${theme.colors.white[100]};
  border-radius: 8px;
`;
