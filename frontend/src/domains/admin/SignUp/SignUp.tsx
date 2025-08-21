import BasicButton from '@/components/BasicButton/BasicButton';
import { ROUTES } from '@/constants';
import AuthLayout from '@/domains/admin/components/AuthLayout/AuthLayout';
import FormField from '@/domains/admin/components/FormField/FormField';
import useAuthForm from '@/domains/admin/hooks/useAuthForm';
import {
  SignUpField,
  signUpFields,
} from '@/domains/admin/SignUp/constants/signUpFields';
import useConfirmPassword from '@/domains/admin/SignUp/hooks/useConfirmPassword';
import useSignup from '@/domains/admin/SignUp/hooks/useSignup';
import {
  fieldContainer,
  signUpCaptionContainer,
  signUpForm,
} from '@/domains/admin/SignUp/SignUp.style';
import {
  validateId,
  validateName,
  validatePassword,
} from '@/domains/admin/utils/authValidations';
import Toast from '@/domains/components/Toast/Toast';
import useNavigation from '@/domains/hooks/useNavigation';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';

type SignUpFieldName = 'name' | 'id' | 'password';

const SIGNUP_INITIAL_VALUES = {
  name: '',
  id: '',
  password: '',
};

const SIGNUP_VALIDATORS = {
  name: validateName,
  id: validateId,
  password: validatePassword,
};

export default function SignUp() {
  const theme = useAppTheme();
  const { goPath } = useNavigation();
  const [toast, setToast] = useState<string | null>(null);

  const {
    authValue: signUpValue,
    handleChangeForm,
    errors,
  } = useAuthForm({
    initValues: SIGNUP_INITIAL_VALUES,
    validators: SIGNUP_VALIDATORS,
  });

  const {
    authValue: confirmPasswordValue,
    handleChangeConfirmPassword,
    errors: confirmPasswordErrors,
  } = useConfirmPassword({
    initValues: { confirmPassword: '', password: signUpValue.password },
  });

  const { handleSignUp, isLoading } = useSignup({
    confirmPasswordErrors,
    errors,
    signUpValue,
    setToast,
  });

  return (
    <AuthLayout title='회원가입' caption='새로운 계정을 만들어보세요'>
      {toast && (
        <Toast message={toast} onClose={() => setToast(null)} duration={2000} />
      )}

      <form css={signUpForm(theme)} onSubmit={handleSignUp}>
        <div css={fieldContainer}>
          {signUpFields.map((field: SignUpField) => {
            const isConfirm = field.name === 'passwordConfirm';

            const props = {
              value: isConfirm
                ? confirmPasswordValue.confirmPassword
                : signUpValue[field.name as SignUpFieldName],
              onChange: isConfirm
                ? handleChangeConfirmPassword
                : handleChangeForm,
              errorMessage: isConfirm
                ? confirmPasswordErrors || ''
                : errors[field.name as SignUpFieldName] || '',
            };

            return (
              <FormField
                type={field.type}
                key={field.name}
                id={field.name}
                label={field.labelKey}
                maxLength={field.maxLength}
                minLength={field.minLength}
                placeholder={field.placeholder}
                {...props}
              />
            );
          })}
        </div>
        <BasicButton disabled={isLoading}>
          {isLoading ? '가입 중...' : '계정 만들기'}
        </BasicButton>
        <div css={signUpCaptionContainer(theme)}>
          <p>
            이미 계정이 있으신가요?
            <strong onClick={() => goPath('/' + ROUTES.LOGIN)}>
              로그인하기
            </strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
