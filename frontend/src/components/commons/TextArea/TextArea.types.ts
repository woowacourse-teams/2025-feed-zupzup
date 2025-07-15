export interface TextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: () => void;
  placeholder: string;
  css: string | string[];
  maxLength: number;
  minLength: number;
}
