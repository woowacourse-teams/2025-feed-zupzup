import { InputProps } from './Input.types';

export default function Input({
  value,
  onChange,
  placeholder,
  css,
}: InputProps) {
  return (
    <input
      css={css}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
    />
  );
}
