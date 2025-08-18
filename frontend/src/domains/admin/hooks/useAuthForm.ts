import { ValidationState } from '@/types/validateState.types';
import { useCallback, useState } from 'react';

interface FormFieldProps<T> {
  initValues: T;
  validators: {
    [K in keyof T]?: (value: T[K]) => ValidationState;
  };
}

export default function useAuthForm<T extends Record<string, string>>({
  initValues,
  validators,
}: FormFieldProps<T>) {
  const [authValue, setAuthValue] = useState<T>(initValues);

  const [errors, setErrors] = useState<{ [K in keyof T]?: string }>({});

  const handleChangeForm = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const name = e.currentTarget.name as keyof T;
      const value = e.currentTarget.value as T[keyof T];

      setAuthValue((prev) => ({
        ...prev,
        [name]: value,
      }));

      if (!validators[name]) return;

      const validate = validators[name];
      if (validate) {
        const result = validate(value);
        setErrors((prev) => {
          const msg = result.ok ? null : result.message;
          if (prev[name] === msg) return prev;
          return { ...prev, [name]: msg };
        });
      }
    },
    []
  );

  return {
    authValue,
    handleChangeForm,
    errors,
  };
}
