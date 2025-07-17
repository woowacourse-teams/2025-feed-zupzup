export interface HeroProps extends React.ComponentProps<'div'> {
  onClick: () => void;
  title: string;
}
