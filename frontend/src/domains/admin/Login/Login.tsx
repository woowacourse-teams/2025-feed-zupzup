import { SEO } from '@/components/SEO/SEO';
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
import useNavigation from '@/domains/hooks/useNavigation';
import { useAppTheme } from '@/hooks/useAppTheme';

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
  const { goPath } = useNavigation();

  const {
    authValue: loginValue,
    handleChangeForm,
    errors,
  } = useAuthForm({
    initValues: LOGIN_INITIAL_VALUES,
    validators: LOGIN_VALIDATORS,
  });

  const { handleSubmit, isLoginPending } = useLogin({ loginValue });

  return (
    <>
      <SEO
        title='로그인'
        description='피드줍줍 관리자 로그인 페이지입니다'
        keywords='로그인, 관리자, 피드줍줍, admin, login'
      />
      <AuthLayout title='로그인' caption='계정에 로그인 하세요'>
        <form
          css={loginForm(theme)}
          onSubmit={handleSubmit}
          noValidate
          aria-label='로그인 폼'
          aria-busy={isLoginPending}
        >
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
          <BasicButton
            type='submit'
            disabled={isLoginPending}
            aria-busy={isLoginPending}
          >
            {isLoginPending ? '로그인 중...' : '로그인'}
          </BasicButton>
          <div css={loginCaptionContainer(theme)}>
            {/* <p>비밀번호를 잊으셨나요?</p> */}
            <p>
              <span aria-hidden='true'>계정이 없으신가요? </span>
              <button
                type='button'
                onClick={() => goPath('/' + ROUTES.SIGN_UP)}
                aria-label='계정이 없으신가요? 회원가입하기'
                css={{
                  background: 'none',
                  border: 'none',
                  padding: 0,
                  font: 'inherit',
                  cursor: 'pointer',
                  textDecoration: 'underline',
                  fontWeight: 700,
                }}
              >
                회원가입하기
              </button>
            </p>
          </div>
        </form>
      </AuthLayout>
    </>
  );
}
