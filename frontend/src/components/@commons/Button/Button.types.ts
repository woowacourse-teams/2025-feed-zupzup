import { SerializedStyles } from '@emotion/react';

export interface ButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  customCSS?: SerializedStyles | SerializedStyles[];
}
