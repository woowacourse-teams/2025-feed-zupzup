/** @jsxImportSource @emotion/react */
import { useAppTheme } from '../../../../../hooks/useAppTheme';
import { BasicButtonProps } from './BasicButton.types';
import {
  basicButtonStyle,
  basicButtonTextStyle,
  basicButtonIconStyle,
} from './BasicButton.styles';
import Button from '../../../../../components/commons/Button/Button';

export default function BasicButton({
  children,
  width,
  icon,
  ...props
}: BasicButtonProps) {
  const theme = useAppTheme();

  return (
    <Button css={[basicButtonStyle(theme, width)]} {...props}>
      {icon && <span css={basicButtonIconStyle}>{icon}</span>}
      <span css={basicButtonTextStyle(theme)}>{children}</span>
    </Button>
  );
}
