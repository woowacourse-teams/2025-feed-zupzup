import { SerializedStyles } from '@emotion/react';

export interface ButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  customCSS?: SerializedStyles | SerializedStyles[];
}

export default function Button({ children, customCSS, ...props }: ButtonProps) {
  return (
    <button css={customCSS} {...props}>
      {children}
    </button>
  );
}
