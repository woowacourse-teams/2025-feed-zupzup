import Input from '@/components/@commons/Input/Input';
import {
  basicInput,
  basicInputContainer,
  caption,
} from '@/components/BasicInput/BasicInput.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface BasicInputProps extends React.ComponentProps<'input'> {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  placeholder: string;
  showCharCount?: boolean;
}

export default function BasicInput({
  name,
  value,
  onChange,
  placeholder,
  maxLength = 10,
  minLength = 1,
  showCharCount = true,
}: BasicInputProps) {
  const theme = useAppTheme();

  return (
    <div css={basicInputContainer}>
      <Input
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        customCSS={basicInput(theme)}
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
