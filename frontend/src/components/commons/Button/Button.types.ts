import { SerializedStyles } from '@emotion/react';

export interface ButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  css: SerializedStyles;
}
