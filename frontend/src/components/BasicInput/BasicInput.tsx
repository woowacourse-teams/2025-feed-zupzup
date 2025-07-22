import Input from '@/components/@commons/Input/Input';
import {
  basicInput,
  basicInputContainer,
  caption,
} from '@/components/BasicInput/BasicInput.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface BasicInputProps {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  placeholder: string;
}

export default function BasicInput({
  value,
  onChange,
  placeholder,
}: BasicInputProps) {
  const theme = useAppTheme();

  return (
    <div css={basicInputContainer}>
      <Input
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        customCSS={basicInput(theme)}
        maxLength={10}
        minLength={1}
      />
      <p css={caption(theme)}>{value.length}/ 10</p>
    </div>
  );
}
