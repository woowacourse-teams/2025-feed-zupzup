import BasicButton from '@/components/BasicButton/BasicButton';
import FormField from '@/domains/admin/login/components/FormField/FormField';
import {
  LoginField,
  loginFields,
} from '@/domains/admin/login/constants/loginFields';
import useAuthForm from '@/domains/admin/login/hooks/useAuthForm';
import {
  fieldContainer,
  login,
  loginCaptionContainer,
  loginContainer,
  loginDescription,
  loginForm,
  loginText,
} from '@/domains/admin/login/Login.style';
import {
  validateName,
  validatePassword,
} from '@/domains/admin/login/utils/loginValidations';
import { useAppTheme } from '@/hooks/useAppTheme';

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
      <div css={loginContainer(theme)}>
        <p css={loginText(theme)}>로그인</p>
        <p css={loginDescription(theme)}>게정에 로그인 하세요</p>
      </div>
      <form css={loginForm(theme)}>
        <div css={fieldContainer}>
          {loginFields.map((field: LoginField) => (
            <FormField
              type={field.type}
              key={field.name}
              id={field.name}
              label={field.labelKey}
              value={loginValue[field.name]}
              onChange={handleChangeForm}
              maxLength={field.maxLength}
              minLength={field.minLength}
              placeholder={field.placeholder}
              errorMessage={errors[field.name] || ''}
            />
          ))}
        </div>
        <BasicButton>로그인</BasicButton>
        <div css={loginCaptionContainer(theme)}>
          <p>비밀번호를 잊으셨나요?</p>
          <p>
            계정이 없으신가요? <strong>회원가입하기</strong>
          </p>
        </div>
      </form>
    </div>
  );
}
