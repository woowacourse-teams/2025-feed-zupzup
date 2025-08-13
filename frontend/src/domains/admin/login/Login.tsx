import BasicButton from '@/components/BasicButton/BasicButton';
import {
  LoginField,
  loginFields,
} from '@/domains/admin/login/constants/loginFields';
import {
  fieldContainer,
  loginCaptionContainer,
  loginForm,
} from '@/domains/admin/login/Login.style';
import AuthLayout from '@/domains/components/AuthLayout/AuthLayout';
import FormField from '@/domains/components/FormField/FormField';
import useAuthForm from '@/domains/hooks/useAuthForm';
import { useAppTheme } from '@/hooks/useAppTheme';
import { validateId, validatePassword } from '@/utils/authValidations';
import { useNavigate } from 'react-router-dom';

export default function Login() {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const loginValidators = {
    id: validateId,
    password: validatePassword,
  };

  const {
    authValue: loginValue,
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
    <AuthLayout title='로그인' caption='계정에 로그인 하세요'>
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
            계정이 없으신가요?{' '}
            <strong onClick={() => navigate('/signup')}>회원가입하기</strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
