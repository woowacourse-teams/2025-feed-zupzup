import { ReactNode } from 'react';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  leftSection,
  iconContainer,
  textContainer,
  listTitle,
  listDescription,
  rightSection,
  container,
} from './SettingListBox.style';

export type SettingListBoxVariant = 'default' | 'danger';
export interface SettingListBoxProps {
  icon: ReactNode;
  title: string;
  description?: string;
  rightElement?: ReactNode;
  onClick?: () => void;
  variant?: SettingListBoxVariant;
}

export default function SettingListBox({
  icon,
  title,
  description,
  rightElement,
  onClick,
  variant = 'default',
}: SettingListBoxProps) {
  const theme = useAppTheme();

  const handleClick = () => {
    if (onClick) {
      onClick();
    }
  };

  return (
    <div
      css={container(theme)}
      onClick={handleClick}
      role={onClick ? 'button' : undefined}
    >
      <div css={leftSection}>
        <div css={iconContainer(theme)}>{icon}</div>
        <div css={textContainer}>
          <div css={listTitle(theme, variant)}>{title}</div>
          <div css={listDescription(theme)}>{description}</div>
        </div>
      </div>
      {rightElement && <div css={rightSection}>{rightElement}</div>}
    </div>
  );
}
