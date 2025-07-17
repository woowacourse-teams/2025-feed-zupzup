export interface HeroProps extends React.ComponentProps<'div'> {
  onLoginClick: () => void;
  onSuggestClick: () => void;
  title: string;
}
