import { SerializedStyles } from '@emotion/react';
import {
  container,
  textIconContainer,
  titleContainer,
  descriptionContainer,
} from './StatusBox.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface StatusBoxProps {
  width?: number | string;
  height?: number | string;
  customCSS?: SerializedStyles;
  textIcon: string;
  title: string;
  description: string;
}

export default function StatusBox({
  width = '100%',
  height = 200,
  textIcon,
  title,
  description,
  customCSS,
}: StatusBoxProps) {
  const theme = useAppTheme();

  return (
    <div css={[container(theme, width, height), customCSS]}>
      <div css={textIconContainer(theme)}>{textIcon}</div>
      <div css={titleContainer(theme)}>{title}</div>
      <div css={descriptionContainer(theme)}>{description}</div>
    </div>
  );
}
