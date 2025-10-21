import Input from '@/components/@commons/Input/Input';
import {
  errorMessageStyle,
  fieldLabel,
  inputFormField,
  loginForm,
} from '@/domains/admin/components/FormField/FormField.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { memo, useState } from 'react';

interface FormFieldProps extends React.ComponentPropsWithoutRef<'input'> {
  id: string;
  label: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  maxLength: number;
  minLength: number;
  placeholder: string;
  errorMessage: string;
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
  errorMessage,
}: FormFieldProps) {
  const theme = useAppTheme();

  const [touched, setTouched] = useState(false);
  const isValidForUI = touched ? !errorMessage : true;
  const errorId = `${id}-error`;
  const hasError = touched && !!errorMessage;

  return (
    <div css={loginForm}>
      <label htmlFor={id} css={fieldLabel(theme)}>
        {label}
      </label>
      <Input
        id={id}
        name={id}
        type={type}
        placeholder={placeholder}
        value={value}
        onFocus={() => setTouched(true)}
        onChange={onChange}
        maxLength={maxLength}
        minLength={minLength}
        customCSS={inputFormField(theme, isValidForUI)}
        aria-invalid={hasError}
        aria-describedby={hasError ? errorId : undefined}
      />
      {errorMessage && (
        <p id={errorId} css={errorMessageStyle(theme)} role='alert'>
          {errorMessage}
        </p>
      )}
    </div>
  );
});
