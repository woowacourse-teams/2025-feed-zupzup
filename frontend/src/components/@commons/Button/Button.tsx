import { SerializedStyles } from '@emotion/react';
import { forwardRef } from 'react';

export interface ButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  customCSS?: SerializedStyles | SerializedStyles[];
}

function ButtonComponent(
  { children, customCSS, ...props }: ButtonProps,
  ref: React.ForwardedRef<HTMLButtonElement>
) {
  return (
    <button ref={ref} css={customCSS} {...props}>
      {children}
    </button>
  );
}

const Button = forwardRef(ButtonComponent);
export default Button;
