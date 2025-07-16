import { ButtonProps } from './Button.types';

export default function Button({ children, css, ...props }: ButtonProps) {
  return (
    <button css={css} {...props}>
      {children}
    </button>
  );
}
