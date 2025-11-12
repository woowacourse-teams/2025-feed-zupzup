import IconButton from '@/components/IconButton/IconButton';
import RefreshIcon from '@/components/icons/RefreshIcon';
import {
  buttonBackgroundStyle,
  feedbackDiffStyle,
  glowButtonStyle,
  refreshButtonLayout,
} from '@/domains/components/RefreshButton/RefreshButton.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface RefreshButtonProps {
  handleRefresh: () => void;
  feedbackDiff: number;
}

export default function RefreshButton({
  handleRefresh,
  feedbackDiff,
}: RefreshButtonProps) {
  const theme = useAppTheme();

  return (
    <div css={refreshButtonLayout}>
      <IconButton
        customCSS={(buttonBackgroundStyle(theme), glowButtonStyle)}
        icon={<RefreshIcon />}
        aria-label='새로고침'
        onClick={handleRefresh}
      />
      <p css={feedbackDiffStyle(theme, feedbackDiff > 9)}>
        {feedbackDiff > 9 ? '9+' : feedbackDiff}
      </p>
    </div>
  );
}
