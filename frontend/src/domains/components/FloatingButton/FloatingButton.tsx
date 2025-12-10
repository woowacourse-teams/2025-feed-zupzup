import Button from '@/components/@commons/Button/Button';
import { floatingButton } from '@/domains/components/FloatingButton/FloatingButton.style';
import { SerializedStyles } from '@emotion/react';
import { memo } from 'react';

export interface Position {
  top?: string;
  right?: string;
  bottom?: string;
  left?: string;
}

interface FloatingButtonProps {
  icon?: React.ReactNode;
  text?: string;
  onClick: () => void;
  inset: Position;
  customCSS?: SerializedStyles | SerializedStyles[];
}

export default memo(function FloatingButton({
  icon,
  text,
  inset,
  customCSS,
  ...props
}: FloatingButtonProps) {
  return (
    <Button css={[floatingButton(inset), customCSS]} {...props}>
      {icon} {text}
    </Button>
  );
});
