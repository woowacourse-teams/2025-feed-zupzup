import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import LockIcon from '@/components/icons/LockIcon';
import {
  secretOption,
  secretText,
} from '@/domains/user/suggestions/components/SecretPostOption/SecretPostOption.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface SecretPostOptionProps extends React.ComponentProps<'button'> {
  isSecret: boolean;
  handleToggleButton: () => void;
}

export default function SecretPostOption({
  isSecret,
  handleToggleButton,
}: SecretPostOptionProps) {
  const theme = useAppTheme();

  return (
    <div css={secretOption(theme)}>
      <BasicToggleButton isToggled={isSecret} onClick={handleToggleButton} />
      <LockIcon />
      <p css={secretText(theme)}>비밀글로 작성</p>
    </div>
  );
}
