import Button from '@/components/@commons/Button/Button';
import { floatingButton } from '@/domains/components/FloatingButton/FloatingButton.style';
import { SerializedStyles } from '@emotion/react';

export interface Position {
  top?: string;
  right?: string;
  bottom?: string;
  left?: string;
}

interface FloatingButtonProps {
  icon: React.ReactNode;
  onClick: () => void;
  inset: Position;
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
