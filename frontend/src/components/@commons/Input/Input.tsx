import { SerializedStyles } from '@emotion/react';

export interface InputProps
  extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'css'> {
  customCSS?: SerializedStyles;
}

export default function Input({
  customCSS,
  type = 'text',
  ...props
}: InputProps) {
  return <input type={type} css={customCSS} {...props} />;
}
