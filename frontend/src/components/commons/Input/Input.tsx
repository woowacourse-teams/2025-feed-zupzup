import { InputProps } from './Input.types';

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
