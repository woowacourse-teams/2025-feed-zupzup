import { validatePasswordConfirm } from '@/utils/authValidations';
import { useEffect, useState } from 'react';

interface UseConfirmPassword {
  initValues: {
    confirmPassword: string;
    password: string;
  };
}

export default function useConfirmPassword({ initValues }: UseConfirmPassword) {
  const [authValue, setAuthValue] =
    useState<Record<string, string>>(initValues);
  const [errors, setErrors] = useState<string>('');

  const handleChangeConfirmPassword = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = e.currentTarget.value;
    setAuthValue((prev) => ({
      ...prev,
      confirmPassword: value,
    }));
    const result = validatePasswordConfirm(value, authValue.password);
    const msg = result.ok ? null : result.message;
    setErrors(msg || '');
  };

  useEffect(() => {
    setAuthValue((prev) => ({
      ...prev,
      password: initValues.password,
    }));

    const result = validatePasswordConfirm(
      authValue.confirmPassword,
      initValues.password
    );
    const msg = result.ok ? null : result.message;
    setErrors(msg || '');
  }, [initValues.password]);

  return {
    authValue,
    handleChangeConfirmPassword,
    errors,
  };
}
