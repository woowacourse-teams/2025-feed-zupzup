import {
  captionContent,
  dashboard,
  dot,
  dotLayout,
  panelContent,
  panelTitle,
} from '@/domains/components/DashboardPanel/DashboardPanel.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export interface DashboardPanelProps {
  title: string;
  content: string;
  caption?: string;
  color: string | undefined;
}

export default function DashboardPanel({
  title,
  content,
  caption,
  color,
}: DashboardPanelProps) {
  const theme = useAppTheme();
  return (
    <div css={dashboard(theme)}>
      <div css={dotLayout}>
        <div css={dot(theme, color)} />
        <p css={panelTitle(theme)}>{title}</p>
      </div>
      <p css={panelContent(theme)}>{content}</p>
      <p css={captionContent(theme)}>{caption}</p>
    </div>
  );
}
