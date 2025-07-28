import {
  currentBar,
  totalBar,
} from '@/components/ProgressBar/ProgressBar.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

interface ProgressBarProps {
  totalStep: number;
  currentStep: number;
}

export default function ProgressBar({
  totalStep,
  currentStep,
}: ProgressBarProps) {
  const theme = useAppTheme();
  const percent = Math.min((currentStep / totalStep) * 100, 100);

  return (
    <div
      css={totalBar}
      role='progressbar'
      aria-valuenow={percent}
      aria-valuemin={0}
      aria-valuemax={100}
    >
      <div css={currentBar(percent, theme)} />
    </div>
  );
}
