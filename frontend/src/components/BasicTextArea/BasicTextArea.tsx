import { useAppTheme } from '@/hooks/useAppTheme';
import TextArea from '../commons/TextArea/TextArea';
import { textArea } from './BasicTextArea.style';
import { BasicTextAreaProps } from './BasicTextArea.type';

export default function BasicTextArea({
  value,
  onChange,
  placeholder,
  maxLength = 500,
  minLength = 10,
}: BasicTextAreaProps) {
  const theme = useAppTheme();
  return (
    <TextArea
      customCSS={textArea(theme)}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      maxLength={maxLength}
      minLength={minLength}
    />
  );
}
