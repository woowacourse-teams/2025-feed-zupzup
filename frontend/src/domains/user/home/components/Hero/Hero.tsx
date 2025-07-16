import BasicButton from '../BasicButton/BasicButton';
import Button from '../Button/Button';
export default function Hero() {
  return (
    <div>
      <h1>Hero</h1>
      <BasicButton width={100} onClick={() => console.log('clicked')}>
        Click me
      </BasicButton>
      <Button />
    </div>
  );
}
