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
  srMessage?: string;
}

export default function DashboardPanel({
  title,
  content,
  caption,
  color,
  srMessage,
}: DashboardPanelProps) {
  const theme = useAppTheme();
  return (
    <>
      <span className='srOnly'>{srMessage}</span>
      <div css={dashboard(theme)} aria-hidden={true}>
        <div css={dotLayout}>
          <div css={dot(theme, color)} />
          <p css={panelTitle(theme)}>{title}</p>
        </div>
        <p css={panelContent(theme)}>{content}</p>
        <p css={captionContent(theme)}>{caption}</p>
      </div>
    </>
  );
}
