export interface InputProps extends React.ComponentProps<'input'> {
  value: string;
  onChange: () => void;
  placeholder: string;
  css: string | string[];
  maxLength: number;
  minLength: number;
}
