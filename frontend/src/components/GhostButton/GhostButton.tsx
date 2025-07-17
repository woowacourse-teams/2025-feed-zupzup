import { ghostButton } from './GhostButton.styles';
import Button from '@/components/commons/Button/Button';
import { GhostButtonProps } from './GhostButton.types';

export default function GhostButton({ icon, text, onClick }: GhostButtonProps) {
  return (
    <Button css={ghostButton} onClick={onClick}>
      {icon}
      <span>{text}</span>
    </Button>
  );
}
