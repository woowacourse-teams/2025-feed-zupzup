import { SerializedStyles } from '@emotion/react';

export interface InputProps extends React.ComponentProps<'input'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onFocus: () => void;
  placeholder: string;
  customCSS: SerializedStyles;
  maxLength: number;
  minLength: number;
}

export default function Input({
  name,
  value,
  onChange,
  onFocus,
  placeholder,
  customCSS,
  maxLength,
  minLength,
}: InputProps) {
  return (
    <input
      name={name}
      css={customCSS}
      maxLength={maxLength}
      minLength={minLength}
      value={value}
      onChange={onChange}
      onFocus={onFocus}
      placeholder={placeholder}
    />
  );
}
