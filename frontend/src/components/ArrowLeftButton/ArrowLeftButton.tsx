import ArrowLeft from '../icons/ArrowLeft';

export interface ArrowLeftButtonProps extends React.ComponentProps<'button'> {
  onClick: () => void;
}

export default function ArrowLeftButton({ onClick }: ArrowLeftButtonProps) {
  return (
    <button onClick={onClick}>
      <ArrowLeft />
    </button>
  );
}
