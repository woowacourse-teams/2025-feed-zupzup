import { useAppTheme } from '@/hooks/useAppTheme';
import TextArea from '../@commons/TextArea/TextArea';
import {
  basicTextAreaContainer,
  caption,
  textArea,
} from './BasicTextArea.style';

export interface BasicTextAreaProps extends React.ComponentProps<'textarea'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
  placeholder: string;
  showCharCount?: boolean;
}

export default function BasicTextArea({
  name,
  value,
  onChange,
  placeholder,
  maxLength = 500,
  minLength = 10,
  showCharCount = true,
}: BasicTextAreaProps) {
  const theme = useAppTheme();
  return (
    <div css={basicTextAreaContainer}>
      <TextArea
        name={name}
        customCSS={textArea(theme)}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        maxLength={maxLength}
        minLength={minLength}
      />
      {showCharCount && (
        <p css={caption(theme)}>
          {value.length} / {maxLength}
        </p>
      )}
    </div>
  );
}
