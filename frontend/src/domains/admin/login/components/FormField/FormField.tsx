import Input from '@/components/@commons/Input/Input';
import useValidate, {
  ValidationState,
} from '@/domains/admin/login/hooks/useValidate';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import { memo, useState } from 'react';

interface FormFieldProps {
  id: string;
  label: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  maxLength: number;
  minLength: number;
  placeholder: string;
  errorMessage?: string;
  validator?: (value: string) => ValidationState;
}

export default memo(function FormField({
  id,
  label,
  value,
  onChange,
  maxLength,
  minLength,
  placeholder,
  errorMessage = '',
  validator,
}: FormFieldProps) {
  const theme = useAppTheme();

  const { validation } = useValidate({
    value,
    validator: validator || (() => ({ ok: true })),
  });

  const [touched, setTouched] = useState(false);
  const isValidForUI = touched ? validation.ok : true;

  return (
    <div>
      <div css={fieldLabel(theme)}>{label}</div>
      <Input
        name={id}
        type='text'
        placeholder={placeholder}
        value={value}
        onFocus={() => setTouched(true)}
        onChange={onChange}
        maxLength={maxLength}
        minLength={minLength}
        customCSS={inputFormField(theme, isValidForUI)}
      />
      <p css={errorMessageStyle(theme)}>{errorMessage} </p>
    </div>
  );
});

export const fieldLabel = (theme: Theme) => css`
  ${theme.typography.pretendard.caption};

  margin-bottom: 4px;
`;

export const inputFormField = (theme: Theme, isValid: boolean) => css`
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  padding: 12px 16px;
  color: ${theme.colors.black[100]};
  background-color: ${theme.colors.white[300]};
  border: 1px solid ${isValid ? theme.colors.gray[100] : theme.colors.red[100]};
  border-radius: 16px;

  ${theme.typography.pretendard.caption};

  &::placeholder {
    color: ${theme.colors.gray[500]};
    ${theme.typography.pretendard.caption};
  }
`;

export const errorMessageStyle = (theme: Theme) => css`
  height: 14px;
  margin-top: 4px;
  color: ${theme.colors.red[100]};

  ${theme.typography.pretendard.captionSmall};
`;
