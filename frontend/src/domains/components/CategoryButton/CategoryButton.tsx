import Button from '@/components/@commons/Button/Button';
import {
  iconCSS,
  container,
} from '@/domains/components/CategoryButton/CategoryButton.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import React from 'react';

interface CategoryButton {
  icon: string | React.ReactNode;
  text: string;
  onClick: () => void;
}

export default function CategoryButton({
  icon,
  text,
  onClick,
}: CategoryButton) {
  const theme = useAppTheme();

  return (
    <Button customCSS={container(theme)} onClick={onClick}>
      <p css={iconCSS}>{icon}</p>
      <p>{text}</p>
    </Button>
  );
}
