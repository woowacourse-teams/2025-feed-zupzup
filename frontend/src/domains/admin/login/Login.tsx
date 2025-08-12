import { PAGE_PADDING_PX } from '@/constants';
import FormField from '@/domains/admin/login/components/FormField/FormField';
import useAuthForm from '@/domains/admin/login/hooks/useAuthForm';
import {
  validateName,
  validatePassword,
} from '@/domains/admin/login/utils/loginValidators';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

export default function Login() {
  const theme = useAppTheme();

  const loginValidators = {
    id: validateName,
    password: validatePassword,
  };

  const {
    value: loginValue,
    handleChangeForm,
    errors,
  } = useAuthForm({
    initValues: {
      id: '',
      password: '',
    },
    validators: loginValidators,
  });

  return (
    <div css={login(theme)}>
      <div css={loginContainer}>
        <p css={loginText(theme)}>로그인</p>
        <p css={loginDescription(theme)}>게정에 로그인 하세요</p>
      </div>
      <form css={loginForm(theme)}>
        {Object.entries(loginValue).map(([key, value]) => (
          <FormField
            key={key}
            id={key}
            label={key === 'id' ? '아이디' : '비밀번호'}
            value={value}
            onChange={handleChangeForm}
            maxLength={key === 'id' ? 10 : 100}
            minLength={5}
            placeholder={
              key === 'id' ? '아이디를 입력해주세요' : '비밀번호를 입력해주세요'
            }
            validator={loginValidators[key as keyof typeof loginValidators]}
            errorMessage={errors[key as keyof typeof errors] || ''}
          />
        ))}
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
