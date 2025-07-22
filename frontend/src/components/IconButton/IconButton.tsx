import React from 'react';
import Button from '../@commons/Button/Button';

interface IconButtonProps extends React.ComponentProps<typeof Button> {
  icon: React.ReactNode;
}

export default function IconButton({ icon, ...props }: IconButtonProps) {
  return <Button {...props}>{icon}</Button>;
}
