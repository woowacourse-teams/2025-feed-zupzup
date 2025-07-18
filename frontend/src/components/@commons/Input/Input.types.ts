import { SerializedStyles } from '@emotion/react';

export interface InputProps extends React.ComponentProps<'input'> {
  value: string;
  onChange: () => void;
  placeholder: string;
  css: SerializedStyles;
  maxLength: number;
  minLength: number;
}
