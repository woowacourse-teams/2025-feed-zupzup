import React from 'react';
import Button from '../@commons/Button/Button';

interface IconButtonProps {
  icon: React.ReactNode;
  onClick?: () => void;
}

export default function IconButton({
  icon,
  onClick,
  ...props
}: IconButtonProps) {
  return (
    <Button onClick={onClick} {...props}>
      {icon}
    </Button>
  );
}
