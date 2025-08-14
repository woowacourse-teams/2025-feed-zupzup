import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import { useMemo } from 'react';

/**
 * StatusBar (Emotion/css)
 */
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
      {/* {showLabel && (
        <div css={labelStyle}>{label ?? `${Math.round(clamped)}%`}</div>
      )} */}
    </div>
  );
}

const wrapperStyle = css`
  width: 100%;
`;

const trackStyle = (theme: Theme) => css`
  position: relative;
  width: 100%;
  height: 12px;
  background-color: ${theme.colors.white[100]}33;
  border-radius: 999px;
`;

const fillStyle = (clamped: number) => css`
  width: ${clamped}%;
  height: 100%;
  background-color: white;
  border-radius: 999px;
`;

const overlayStyle = css`
  border-radius: 999px;
`;
