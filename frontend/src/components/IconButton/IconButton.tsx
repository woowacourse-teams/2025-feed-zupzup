import React from 'react';
import Button from '../@commons/Button/Button';

interface IconButtonProps {
  icon: React.ReactNode;
  onClick?: () => void;
}

export default function IconButton({ icon, onClick }: IconButtonProps) {
  return <Button onClick={onClick}>{icon}</Button>;
}
