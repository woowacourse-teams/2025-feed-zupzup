import { SerializedStyles } from '@emotion/react';

export interface TextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  placeholder: string;
  customCSS: SerializedStyles;
  maxLength: number;
  minLength: number;
}

export default function TextArea({
  name,
  value,
  onChange,
  placeholder,
  customCSS,
  maxLength,
  minLength,
}: TextAreaProps) {
  return (
    <textarea
      name={name}
      css={customCSS}
      minLength={minLength}
      maxLength={maxLength}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
    />
  );
}
