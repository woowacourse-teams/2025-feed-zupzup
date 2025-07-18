import { TextAreaProps } from './TextArea.types';

export default function TextArea({
  value,
  onChange,
  placeholder,
  customCSS,
  maxLength,
  minLength,
}: TextAreaProps) {
  return (
    <textarea
      css={customCSS}
      minLength={minLength}
      maxLength={maxLength}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
    />
  );
}
