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
      <div css={dotLayout}>
        <div css={dot(theme)} />
        <p css={panelTitle(theme)}>{title}</p>
      </div>
      <p css={panelContent(theme)}>{content}</p>
      <p css={captionContent(theme)}>{caption}</p>
    </div>
  );
}

const dashboard = (theme: Theme) => css`
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 16px;
  background-color: ${theme.colors.white[300]};
  border-radius: 16px;
`;

const dotLayout = css`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const dot = (theme: Theme) => css`
  width: 8px;
  height: 8px;
  background-color: ${theme.colors.gray[600]};
  border-radius: 50%;
`;

const panelTitle = (theme: Theme) => css`
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[600]};
`;

const panelContent = (theme: Theme) => css`
  ${theme.typography.bmHannaPro.bodyLarge};

  font-weight: 900;
  color: ${theme.colors.black};
`;

const captionContent = (theme: Theme) => css`
  ${theme.typography.inter.caption}

  color: ${theme.colors.gray[600]};
`;
