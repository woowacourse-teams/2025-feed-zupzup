import { useEffect, useState } from 'react';

interface ValidatorProps {
  value: string;
  validator: (value: string) => ValidationState;
}

export type ValidationState = { ok: true } | { ok: false; message: string };

export default function useValidate({ value, validator }: ValidatorProps) {
  const [validation, setValidation] = useState<ValidationState>({ ok: true });

  useEffect(() => {
    setValidation(validator(value));
  }, [value, validator]);

  return { validation };
}
