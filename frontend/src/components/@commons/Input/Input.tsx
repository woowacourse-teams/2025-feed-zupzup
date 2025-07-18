import { SerializedStyles } from '@emotion/react';

export interface InputProps extends React.ComponentProps<'input'> {
  value: string;
  onChange: () => void;
  placeholder: string;
  css: SerializedStyles;
  maxLength: number;
  minLength: number;
}

export default function Input({
  value,
  onChange,
  placeholder,
  css,
  maxLength,
  minLength,
}: InputProps) {
  return (
    <input
      css={css}
      maxLength={maxLength}
      minLength={minLength}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
    />
  );
}
