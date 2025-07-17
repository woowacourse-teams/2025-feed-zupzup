import { SerializedStyles } from '@emotion/react';

export interface TextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  placeholder: string;
  customCSS: SerializedStyles;
  maxLength: number;
  minLength: number;
}
