import {
  captionContent,
  dashboard,
  dot,
  dotLayout,
  panelContent,
  panelTitle,
} from '@/domains/components/DashboardPanel/DashboardPanel.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface DashboardPanelProps {
  title: string;
  content: string;
  caption?: string;
}

export default function DashboardPanel({
  title,
  content,
  caption,
}: DashboardPanelProps) {
  const theme = useAppTheme();
  return (
    <div css={dashboard(theme)}>
      <div css={dotLayout}>
        <div css={dot(theme)} />
        <p css={panelTitle(theme)}>{title}</p>
      </div>
      <p css={panelContent(theme)}>{content}</p>
      <p css={captionContent(theme)}>{caption}</p>
    </div>
  );
}
