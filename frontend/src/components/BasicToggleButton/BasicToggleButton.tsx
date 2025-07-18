import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

interface ToggleButtonProps {
  isOn: boolean;
  onClick: () => void;
}

export default function BasicToggleButton({
  isOn,
  onClick,
}: ToggleButtonProps) {
  const theme = useAppTheme();
  return (
    <>
      <div css={toggleContainer(theme)} onClick={onClick}>
        <div
          className={`toggle-container ${isOn ? 'toggle--checked' : null}`}
        />
        <div className={`toggle-circle ${isOn ? 'toggle--checked' : null}`} />
      </div>
    </>
  );
}

const toggleContainer = (theme: Theme) => css`
  position: relative;
  cursor: pointer;

  > .toggle-container {
    width: 50px;
    height: 24px;
    border-radius: 30px;
    background-color: rgb(224, 224, 224);
  }

  > .toggle--checked {
    background-color: ${theme.colors.yellow[200]};
    transition: 0.5s;
  }

  > .toggle-circle {
    position: absolute;
    top: 2px;
    left: 1px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background-color: rgb(255, 254, 255);
    transition: 0.5s;
  }
  > .toggle--checked {
    left: 27px;
    transition: 0.5s;
  }
`;
