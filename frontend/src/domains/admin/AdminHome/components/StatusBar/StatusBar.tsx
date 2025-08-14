import {
  fillStyle,
  overlayStyle,
  wrapperStyle,
  trackStyle,
} from '@/domains/admin/AdminHome/components/StatusBar/StatusBar.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useMemo } from 'react';

export default function StatusBar({ status }: { status: number }) {
  const clamped = useMemo(() => Math.max(0, Math.min(100, status)), [status]);

  const theme = useAppTheme();

  return (
    <div css={wrapperStyle}>
      <div
        css={trackStyle(theme)}
        role='progressbar'
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={clamped}
      >
        <div css={fillStyle(clamped)} />
        <div css={overlayStyle} />
      </div>
    </div>
  );
}
