import BasicButton from '@/components/BasicButton/BasicButton';
import { ROUTES } from '@/constants';
import AuthLayout from '@/domains/admin/components/AuthLayout/AuthLayout';
import FormField from '@/domains/admin/components/FormField/FormField';
import useAuthForm from '@/domains/admin/hooks/useAuthForm';
import {
  LoginField,
  loginFields,
} from '@/domains/admin/Login/constants/loginFields';
import useLogin from '@/domains/admin/Login/hooks/useLogin';
import {
  fieldContainer,
  loginCaptionContainer,
  loginForm,
} from '@/domains/admin/Login/Login.style';
import {
  validateId,
  validatePassword,
} from '@/domains/admin/utils/authValidations';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useNavigate } from 'react-router-dom';

const LOGIN_INITIAL_VALUES = {
  id: '',
  password: '',
};

const LOGIN_VALIDATORS = {
  id: validateId,
  password: validatePassword,
};

export default function Login() {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const {
    authValue: loginValue,
    handleChangeForm,
    errors,
  } = useAuthForm({
    initValues: LOGIN_INITIAL_VALUES,
    validators: LOGIN_VALIDATORS,
  });

  const { handleSubmit } = useLogin({ loginValue });

  return (
    <AuthLayout title='로그인' caption='계정에 로그인 하세요'>
      <form css={loginForm(theme)} onSubmit={handleSubmit}>
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
            계정이 없으신가요?
            <strong onClick={() => navigate('/' + ROUTES.SIGN_UP)}>
              회원가입하기
            </strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
