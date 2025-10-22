import { SerializedStyles } from '@emotion/react';

export interface InputProps extends React.ComponentProps<'input'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onFocus?: () => void;
  placeholder: string;
  customCSS: SerializedStyles;
  maxLength: number;
  minLength: number;
}

export default function Input({ customCSS, ...rest }: InputProps) {
  return <input css={customCSS} {...rest} />;
}
