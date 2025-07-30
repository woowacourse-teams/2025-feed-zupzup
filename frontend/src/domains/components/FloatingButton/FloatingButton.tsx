import Button from '@/components/@commons/Button/Button';
import {
  floatingButton,
  floatingButtonIcon,
} from '@/domains/components/FloatingButton/FloatingButton.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface FloatingButtonProps {
  icon: React.ReactNode;
  onClick: () => void;
}

export default function FloatingButton({ icon, onClick }: FloatingButtonProps) {
  const theme = useAppTheme();

  return (
    <Button onClick={onClick} css={floatingButton(theme)}>
      <div css={floatingButtonIcon}>{icon}</div>
    </Button>
  );
}
