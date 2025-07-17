export interface BasicButtonProps extends React.ComponentProps<'button'> {
  children: React.ReactNode;
  width?: string | number;
  icon?: React.ReactNode;
  variant?: 'primary' | 'secondary';
}
