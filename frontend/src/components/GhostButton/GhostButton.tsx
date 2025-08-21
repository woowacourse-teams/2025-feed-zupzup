import { ghostButton } from './GhostButton.styles';
import Button from '@/components/@commons/Button/Button';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface GhostButtonProps extends React.ComponentProps<'button'> {
  icon: React.ReactNode;
  text: string;
}

export default function GhostButton({ icon, text, onClick }: GhostButtonProps) {
  const theme = useAppTheme();

  return (
    <Button css={ghostButton(theme)} onClick={onClick}>
      {icon}
      <span>{text}</span>
    </Button>
  );
}
