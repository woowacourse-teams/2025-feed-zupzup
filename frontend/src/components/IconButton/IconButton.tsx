import React from 'react';
import Button from '../@commons/Button/Button';

interface IconButtonProps {
  icon: React.ReactNode;
  onClick?: () => void;
  ariaLabel?: string;
}

export default function IconButton({
  icon,
  onClick,
  ariaLabel,
}: IconButtonProps) {
  return (
    <Button onClick={onClick} aria-label={ariaLabel}>
      {icon}
    </Button>
  );
}
