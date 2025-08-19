import BasicButton from '@/components/BasicButton/BasicButton';
import DownloadIcon from '@/components/icons/DownloadIcon';
import {
  QRCodeContainer,
  QRImageContainer,
  QRTitle,
} from '@/domains/admin/components/QRModal/components/QRImageSection/QRImageSection.styles';
import { QRText } from '@/domains/admin/components/QRModal/QRModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useQRImageDownload } from './hooks/useQRImageDownload';
import { urlDownload } from '@/domains/utils/urlDownload';

type QRImageSectionProps = {
  url: string;
};

export default function QRImageSection({ url }: QRImageSectionProps) {
  const theme = useAppTheme();
  const { downloadUrl } = useQRImageDownload();

  const handleDownload = async () => {
    if (!downloadUrl) return;
    urlDownload({ downloadUrl, fileName: 'qr-code.png' });
  };

  return (
    <div css={QRCodeContainer}>
      <p css={QRTitle(theme)}>QR 코드 & URL 공유</p>

      <div css={QRImageContainer}>
        <img src={url} alt='피드백 페이지로 이동하는 QR 코드' />
      </div>

      <p css={QRText(theme)}>
        위 코드를 스캔하여 피드백 페이지로 이동할 수 있습니다.
      </p>

      <BasicButton
        variant='secondary'
        padding='8px 8px'
        fontSize={12}
        height={30}
        width='70%'
        icon={<DownloadIcon />}
        onClick={handleDownload}
      >
        QR 코드 다운로드
      </BasicButton>
    </div>
  );
}
