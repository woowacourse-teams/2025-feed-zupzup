import { SerializedStyles } from '@emotion/react';

export interface TextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: () => void;
  placeholder: string;
  css: SerializedStyles;
  maxLength: number;
  minLength: number;
}
