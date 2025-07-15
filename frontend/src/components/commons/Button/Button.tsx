interface ButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  css: string | string[];
}

export default function Button({ children, onClick, css }: ButtonProps) {
  return (
    <button css={css} onClick={onClick}>
      {children}
    </button>
  );
}
