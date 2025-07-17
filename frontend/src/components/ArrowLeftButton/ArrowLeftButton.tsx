import ArrowLeft from '../icons/ArrowLeft';
import { ArrowLeftButtonProps } from './ArrowLeftButton.type';

export default function ArrowLeftButton({ onClick }: ArrowLeftButtonProps) {
  return (
    <button onClick={onClick}>
      <ArrowLeft />
    </button>
  );
}
