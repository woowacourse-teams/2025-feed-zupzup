import Button from '@/components/@commons/Button/Button';
import { floatingButton } from '@/domains/components/FloatingButton/FloatingButton.style';
import { SerializedStyles } from '@emotion/react';

interface FloatingButtonProps {
  icon: React.ReactNode;
  onClick: () => void;
  inset: string[];
  customCSS?: SerializedStyles | SerializedStyles[];
}
export default function FloatingButton({
  icon,
  onClick,
  inset,
  customCSS,
}: FloatingButtonProps) {
  return (
    <Button onClick={onClick} css={[floatingButton(inset), customCSS]}>
      {icon}
    </Button>
  );
}
