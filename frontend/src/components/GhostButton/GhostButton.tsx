import { ghostButton } from './GhostButton.styles';
import Button from '@/components/@commons/Button/Button';
import { GhostButtonProps } from './GhostButton.types';
import { useAppTheme } from '@/hooks/useAppTheme';

export default function GhostButton({ icon, text, onClick }: GhostButtonProps) {
  const theme = useAppTheme();

  return (
    <Button css={ghostButton(theme)} onClick={onClick}>
      {icon}
      <span>{text}</span>
    </Button>
  );
}
