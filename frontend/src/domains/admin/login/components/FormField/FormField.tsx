import Input from '@/components/@commons/Input/Input';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import { memo, useState } from 'react';

interface FormFieldProps extends React.ComponentPropsWithoutRef<'input'> {
  id: string;
  label: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  maxLength: number;
  minLength: number;
  placeholder: string;
  errorMessage?: string;
}

export default memo(function FormField({
  id,
  label,
  type,
  value,
  onChange,
  maxLength,
  minLength,
  placeholder,
  errorMessage = '',
}: FormFieldProps) {
  const theme = useAppTheme();

  const [touched, setTouched] = useState(false);
  const isValidForUI = touched ? !errorMessage : true;

  return (
    <div>
      <div css={fieldLabel(theme)}>{label}</div>
      <Input
        name={id}
        type={type}
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
