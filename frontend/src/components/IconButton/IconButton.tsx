import React from 'react';
import Button from '../@commons/Button/Button';

interface IconButtonProps {
  icon: React.ReactNode;
}

export default function IconButton({ icon }: IconButtonProps) {
  return <Button>{icon}</Button>;
}
