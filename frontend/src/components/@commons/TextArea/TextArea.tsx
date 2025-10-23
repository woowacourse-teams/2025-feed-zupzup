import { SerializedStyles } from '@emotion/react';
import { forwardRef } from 'react';

export interface TextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  placeholder: string;
  customCSS: SerializedStyles;
  maxLength: number;
  minLength: number;
}

function TextAreaComponent(
  {
    name,
    value,
    onChange,
    placeholder,
    customCSS,
    maxLength,
    minLength,
  }: TextAreaProps,
  ref: React.Ref<HTMLTextAreaElement>
) {
  return (
    <textarea
      ref={ref}
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

const TextArea = forwardRef(TextAreaComponent);

export default TextArea;
