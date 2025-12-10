import { SerializedStyles } from '@emotion/react';
import {
  container,
  textIconContainer,
  titleContainer,
  descriptionContainer,
} from './StatusBox.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface StatusBoxProps {
  width?: string;
  height?: string;
  customCSS?: SerializedStyles;
  textIcon: string;
  title: string;
  description: string;
}

export default function StatusBox({
  width = '100%',
  height = '200px',
  textIcon,
  title,
  description,
  customCSS,
}: StatusBoxProps) {
  const theme = useAppTheme();

  return (
    <div css={[container(theme, width, height), customCSS]}>
      <div css={textIconContainer(theme)} aria-hidden={true}>
        {textIcon}
      </div>
      <div css={titleContainer(theme)}>{title}</div>
      <div css={descriptionContainer(theme)}>{description}</div>
    </div>
  );
}
