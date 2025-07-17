import { useAppTheme } from '../../hooks/useAppTheme';
import { BasicButtonProps } from './BasicButton.types';
import {
  basicButton,
  basicButtonText,
  basicButtonIcon,
} from './BasicButton.styles';
import Button from '@/components/commons/Button/Button';

export default function BasicButton({
  children,
  width = '100%',
  icon,
  variant = 'primary',
}: BasicButtonProps) {
  const theme = useAppTheme();

  return (
    <Button css={basicButton(theme, width, variant)}>
      {icon && <span css={basicButtonIcon}>{icon}</span>}
      <span css={basicButtonText(theme, variant)}>{children}</span>
    </Button>
  );
}
