import BasicButton from '@/components/BasicButton/BasicButton';
import {
  validateId,
  validateName,
  validatePassword,
} from '@/utils/authValidations';
import {
  SignUpField,
  signUpFields,
} from '@/domains/admin/signUp/constants/signUpFields';
import {
  fieldContainer,
  signUpCaptionContainer,
  signUpForm,
} from '@/domains/admin/signUp/SignUp.style';
import AuthLayout from '@/domains/components/AuthLayout/AuthLayout';
import FormField from '@/domains/components/FormField/FormField';
import { useAppTheme } from '@/hooks/useAppTheme';
import useAuthForm from '@/domains/hooks/useAuthForm';
import { useNavigate } from 'react-router-dom';

export default function SignUp() {
  const theme = useAppTheme();
  const navigate = useNavigate();

  const signUpValidators = {
    name: validateName,
    id: validateId,
    password: validatePassword,
    passwordConfirm: validatePassword,
  };

  const {
    value: signUpValue,
    handleChangeForm,
    errors,
  } = useAuthForm({
    initValues: {
      name: '',
      id: '',
      password: '',
      passwordConfirm: '',
    },
    validators: signUpValidators,
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
              value={signUpValue[field.name]}
              onChange={handleChangeForm}
              maxLength={field.maxLength}
              minLength={field.minLength}
              placeholder={field.placeholder}
              errorMessage={errors[field.name] || ''}
            />
          ))}
        </div>
        <BasicButton>계정 만들기</BasicButton>
        <div css={signUpCaptionContainer(theme)}>
          <p>
            이미 계정이 있으신가요?
            <strong onClick={() => navigate('/login')}>로그인하기</strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
