import { SerializedStyles } from '@emotion/react';

export interface TextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: () => void;
  placeholder: string;
  css: SerializedStyles;
  maxLength: number;
  minLength: number;
}

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
