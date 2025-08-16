import BasicButton from '@/components/BasicButton/BasicButton';
import { ROUTES } from '@/constants';
import AuthLayout from '@/domains/admin/components/AuthLayout/AuthLayout';
import FormField from '@/domains/admin/components/FormField/FormField';
import {
  SignUpField,
  signUpFields,
} from '@/domains/admin/SignUp/constants/signUpFields';
import useConfirmPassword from '@/domains/admin/SignUp/hooks/useConfirmPassword';
import {
  fieldContainer,
  signUpCaptionContainer,
  signUpForm,
} from '@/domains/admin/SignUp/SignUp.style';
import useAuthForm from '@/domains/admin/hooks/useAuthForm';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  validateId,
  validateName,
  validatePassword,
} from '@/domains/admin/utils/authValidations';
import { useNavigate } from 'react-router-dom';
import Toast from '@/domains/components/Toast/Toast';
import { FormEvent, useState } from 'react';
import { postAdminSignup } from '@/apis/admin.api';
import { setLocalStorage } from '@/utils/localStorage';

type SignUpFieldName = 'name' | 'id' | 'password';

export default function SignUp() {
  const theme = useAppTheme();
  const navigate = useNavigate();
  const [toast, setToast] = useState<string | null>(null);

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

  const handleSignUp = async (event: FormEvent) => {
    event.preventDefault();
    const isValid = Object.values(errors).every((error) => !error);
    const isConfirmPasswordValid =
      !confirmPasswordErrors && signUpValue.password !== '';

    if (!isValid || !isConfirmPasswordValid) {
      setToast('입력하신 정보를 다시 확인해주세요.');
      return;
    } else {
      await postAdminSignup({
        loginId: signUpValue.id,
        password: signUpValue.password,
        adminName: signUpValue.name,
        onError: () => {
          setToast('회원가입에 실패했습니다. 다시 시도해주세요.');
        },
        onSuccess: (authData: Response) => {
          setToast('회원가입이 완료되었습니다.');
          navigate(ROUTES.ADMIN + '/' + ROUTES.ADMIN_HOME);
          setLocalStorage('auth', authData);
        },
      });
    }
  };

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
        <BasicButton>계정 만들기</BasicButton>
        <div css={signUpCaptionContainer(theme)}>
          <p>
            이미 계정이 있으신가요?
            <strong onClick={() => navigate('/' + ROUTES.LOGIN)}>
              로그인하기
            </strong>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
}
