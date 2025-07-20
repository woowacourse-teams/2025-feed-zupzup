import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';
import { secretOption, secretText } from './SecretPostOption.style';
import Lock from '@/components/icons/Lock';

export default function SecretPostOption() {
  const theme = useAppTheme();
  const [isOn, setisOn] = useState(false);

  const toggleHandler = () => {
    setisOn(!isOn);
  };

  return (
    <div css={secretOption(theme)}>
      <BasicToggleButton isOn={isOn} onClick={toggleHandler} />
      <Lock />
      <p css={secretText(theme)}>비밀글로 작성</p>
    </div>
  );
}
