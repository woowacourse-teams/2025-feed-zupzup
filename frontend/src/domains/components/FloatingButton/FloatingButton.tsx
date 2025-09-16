import Button from '@/components/@commons/Button/Button';
import { floatingButton } from '@/domains/components/FloatingButton/FloatingButton.style';
import { SerializedStyles } from '@emotion/react';
import React from 'react';

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

function FloatingButton({
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

export default React.memo(FloatingButton);
