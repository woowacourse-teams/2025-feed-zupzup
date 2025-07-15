import { ButtonProps } from './Button.types';

export default function Button({ children, onClick, css }: ButtonProps) {
  return (
    <button css={css} onClick={onClick}>
      {children}
    </button>
  );
}
