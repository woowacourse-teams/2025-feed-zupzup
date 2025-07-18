import { TextAreaProps } from './TextArea.types';

export default function TextArea({
  value,
  onChange,
  placeholder,
  css,
  maxLength,
  minLength,
}: TextAreaProps) {
  return (
    <textarea
      minLength={minLength}
      maxLength={maxLength}
      css={css}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
    />
  );
}
