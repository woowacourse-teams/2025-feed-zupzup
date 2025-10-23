import { useAppTheme } from '@/hooks/useAppTheme';
import {
  container,
  qrImageSkeleton,
  textSkeleton,
  buttonSkeleton,
  urlContainer,
  urlBoxSkeleton,
  copyButtonSkeleton,
} from '@/domains/admin/components/QRModal/components/QRModalSkeleton/QRModalSkeleton.styles';

// QR 이미지 섹션 스켈레톤
export function QRImageSkeleton() {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={qrImageSkeleton(theme)} />
    </div>
  );
}

function QRUrlSkeleton() {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <div css={textSkeleton(theme, 'short')} />
      <div css={urlContainer}>
        <div css={urlBoxSkeleton(theme)} />
        <div css={copyButtonSkeleton(theme)} />
      </div>
      <div css={textSkeleton(theme, 'long')} />
    </div>
  );
}

export default function QRModalSkeleton() {
  const theme = useAppTheme();

  return (
    <div css={container}>
      <QRImageSkeleton />
      <QRUrlSkeleton />
      <div css={buttonSkeleton(theme)} />
    </div>
  );
}
