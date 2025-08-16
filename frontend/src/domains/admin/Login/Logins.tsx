import BasicButton from '@/components/BasicButton/BasicButton';
import { ROUTES } from '@/constants';
import AuthLayout from '@/domains/admin/components/AuthLayout/AuthLayout';
import FormField from '@/domains/admin/components/FormField/FormField';
import {
  LoginField,
  loginFields,
} from '@/domains/admin/Login/constants/loginFields';
import {
  fieldContainer,
  loginCaptionContainer,
  loginForm,
} from '@/domains/admin/Login/Login.style';
import useAuthForm from '@/domains/admin/hooks/useAuthForm';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  validateId,
  validatePassword,
} from '@/domains/admin/utils/authValidations';
import { useNavigate } from 'react-router-dom';
import { postAdminLogin } from '@/apis/admin.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { setLocalStorage } from '@/utils/localStorage';

export default function Login() {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const { showErrorModal } = useErrorModalContext();

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

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const response = await postAdminLogin({
      loginId: loginValue.id,
      password: loginValue.password,
      onError: () => {
        showErrorModal(
          new Error('로그인 실패'),
          '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.'
        );
      },
      onSuccess: () => {
        navigate(ROUTES.ADMIN_HOME);
      },
    });

    if (response) {
      setLocalStorage('auth', response.data);
    }
  };

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
            <strong onClick={() => navigate(ROUTES.SIGN_UP)}>
              회원가입하기
            </strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
