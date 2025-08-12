import Input from '@/components/@commons/Input/Input';
import useValidate, {
  ValidationState,
} from '@/domains/admin/login/hooks/useValidate';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

interface FormFieldProps {
  label: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  maxLength: number;
  minLength: number;
  validator?: (value: string) => ValidationState;
}

export default function FormField({
  label,
  value,
  onChange,
  maxLength,
  minLength,
  validator,
}: FormFieldProps) {
  const theme = useAppTheme();

  const { validation } = useValidate({
    value,
    validator: validator || (() => ({ ok: true })),
  });

  return (
    <div>
      <div css={fieldLabel(theme)}>{label}</div>
      <Input
        type='text'
        placeholder='아이디를 입력해주세요'
        value={value}
        onChange={onChange}
        maxLength={maxLength}
        minLength={minLength}
        customCSS={inputFormField(theme, validation.ok)}
      />
    </div>
  );
}

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
  background-color: ${theme.colors.white[300]};
  border: 1px solid ${isValid ? theme.colors.gray[100] : theme.colors.red[100]};
  border-radius: 16px;

  ${theme.typography.pretendard.caption};

  &::placeholder {
    color: ${theme.colors.gray[500]};
    ${theme.typography.pretendard.caption};
  }
`;
