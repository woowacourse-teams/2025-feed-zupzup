import Lock from '@/components/icons/lock';
import { useAppTheme } from '@/hooks/useAppTheme';
import { css } from '@emotion/react';
import { secretOption, secretText } from './SecretPostOption.style';

export default function SecretPostOption() {
  const theme = useAppTheme();
  return (
    <div css={secretOption(theme)}>
      <div
        css={css`
          background-color: blueviolet;
        `}
      >
        lockButton
      </div>
      <Lock />
      <p css={secretText(theme)}>비밀글로 작성</p>
    </div>
  );
}
