import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';

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
      <div>
        <div css={dot(theme)} />
        <p css={panelTitle(theme)}>{title}</p>
      </div>
      <p>{content}</p>
      <p>{caption}</p>
    </div>
  );
}

const dashboard = (theme: Theme) => css`
  padding: 16px;
  background-color: ${theme.colors.white[300]};
  border-radius: 16px;
`;

const dot = (theme: Theme) => css`
  width: 8px;
  height: 8px;
  background-color: ${theme.colors.gray[200]};
  border-radius: 50%;
`;

const panelTitle = (theme: Theme) => css`
  ${theme.typography.inter.small}
`;
