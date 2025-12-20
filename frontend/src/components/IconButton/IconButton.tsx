import React from 'react';
import Button from '../@commons/Button/Button';
import { SerializedStyles } from '@emotion/react';

interface IconButtonProps {
  icon: React.ReactNode;
  onClick?: () => void;
  customCSS?: SerializedStyles | SerializedStyles[];
}

export default function IconButton({
  icon,
  onClick,
  customCSS,
  ...props
}: IconButtonProps) {
  return (
    <Button customCSS={customCSS ?? []} onClick={onClick} {...props}>
      {icon}
    </Button>
  );
}
