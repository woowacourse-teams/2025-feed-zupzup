import { ghostButton } from './GhostButton.styles';
import Button from '@/components/commons/Button/Button';
export default function GhostButton({
  icon,
  text,
}: {
  icon: React.ReactNode;
  text: string;
}) {
  return (
    <Button css={ghostButton}>
      {icon}
      <span>{text}</span>
    </Button>
  );
}
