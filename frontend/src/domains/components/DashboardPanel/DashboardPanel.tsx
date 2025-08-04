import RightArrowIcon from '@/components/icons/RightArrowIcon';
import {
  button,
  captionContent,
  dashboard,
  dot,
  dotLayout,
  panelContent,
  panelTitle,
  topContainer,
} from '@/domains/components/DashboardPanel/DashboardPanel.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface DashboardPanelProps {
  title: string;
  content: string;
  caption?: string;
  color: string | undefined;
  isClick: boolean;
  onClick: (() => void) | undefined;
  isButton: boolean;
}

export default function DashboardPanel({
  title,
  content,
  caption,
  color,
  isClick,
  onClick,
  isButton,
}: DashboardPanelProps) {
  const theme = useAppTheme();
  return (
    <div
      css={[dashboard(theme, isClick), isButton && button]}
      onClick={onClick}
    >
      <div css={topContainer}>
        <div css={dotLayout}>
          <div css={dot(theme, color)} />
          <p css={panelTitle(theme)}>{title}</p>
        </div>
        {isButton && <RightArrowIcon />}
      </div>
      <p css={panelContent(theme, isClick)}>{content}</p>
      <p css={captionContent(theme)}>{caption}</p>
    </div>
  );
}
