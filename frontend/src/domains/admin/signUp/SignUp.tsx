import BasicButton from '@/components/BasicButton/BasicButton';
import { ROUTES } from '@/constants';
import {
  SignUpField,
  signUpFields,
} from '@/domains/admin/signUp/constants/signUpFields';
import useConfirmPassword from '@/domains/admin/signUp/hooks/useConfirmPassword';
import {
  fieldContainer,
  signUpCaptionContainer,
  signUpForm,
} from '@/domains/admin/signUp/SignUp.style';
import AuthLayout from '@/domains/components/AuthLayout/AuthLayout';
import FormField from '@/domains/components/FormField/FormField';
import useAuthForm from '@/domains/hooks/useAuthForm';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  validateId,
  validateName,
  validatePassword,
} from '@/utils/authValidations';
import { useNavigate } from 'react-router-dom';

export default function SignUp() {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const signUpValidators = {
    name: validateName,
    id: validateId,
    password: validatePassword,
  };

  const {
    authValue: signUpValue,
    handleChangeForm,
    errors,
  } = useAuthForm({
    initValues: {
      name: '',
      id: '',
      password: '',
    },
    validators: signUpValidators,
  });

  const {
    authValue: confirmPasswordValue,
    handleChangeConfirmPassword,
    errors: confirmPasswordErrors,
  } = useConfirmPassword({
    initValues: { confirmPassword: '', password: signUpValue.password },
  });

  return (
    <AuthLayout title='회원가입' caption='새로운 계정을 만들어보세요'>
      <form css={signUpForm(theme)}>
        <div css={fieldContainer}>
          {signUpFields.map((field: SignUpField) => (
            <FormField
              type={field.type}
              key={field.name}
              id={field.name}
              label={field.labelKey}
              value={
                field.name === 'passwordConfirm'
                  ? confirmPasswordValue.confirmPassword
                  : signUpValue[field.name]
              }
              onChange={
                field.name === 'passwordConfirm'
                  ? handleChangeConfirmPassword
                  : handleChangeForm
              }
              maxLength={field.maxLength}
              minLength={field.minLength}
              placeholder={field.placeholder}
              errorMessage={
                field.name === 'passwordConfirm'
                  ? confirmPasswordErrors || ''
                  : errors[field.name] || ''
              }
            />
          ))}
        </div>
        <BasicButton>계정 만들기</BasicButton>
        <div css={signUpCaptionContainer(theme)}>
          <p>
            이미 계정이 있으신가요?
            <strong onClick={() => navigate(ROUTES.LOGIN)}>로그인하기</strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
