import { useAppTheme } from '@/hooks/useAppTheme';

import { textArea } from './BasicTextArea.style';
import { BasicTextAreaProps } from './BasicTextArea.type';
import TextArea from '../@commons/TextArea/TextArea';

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
