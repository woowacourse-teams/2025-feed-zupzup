import { ButtonProps } from './Button.types';

export default function Button({ children, customCSS, ...props }: ButtonProps) {
  return (
    <button css={customCSS} {...props}>
      {children}
    </button>
  );
}
