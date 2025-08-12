import FormField from '@/domains/admin/login/components/FormField/FormField';
import {
  LoginField,
  loginFields,
} from '@/domains/admin/login/constants/loginFields';
import useAuthForm from '@/domains/admin/login/hooks/useAuthForm';
import {
  login,
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
      <div css={loginContainer}>
        <p css={loginText(theme)}>로그인</p>
        <p css={loginDescription(theme)}>게정에 로그인 하세요</p>
      </div>
      <form css={loginForm(theme)}>
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
      </form>
    </div>
  );
}
