import BasicButton from '@/components/BasicButton/BasicButton';
import CopyIcon from '@/components/icons/CopyIcon';
import { useToast } from '@/contexts/useToast';
import {
  copyButton,
  QRURLContainer,
  URLBox,
  URLContainer,
} from '@/domains/admin/components/QRModal/components/QRUrlSection/QRUrlsection.styles';
import { QRText } from '@/domains/admin/components/QRModal/QRModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

type QRUrlSectionProps = {
  url: string;
};

export default function QRUrlSection({ url }: QRUrlSectionProps) {
  const theme = useAppTheme();
  const { showToast } = useToast();

  const handleCopy = async () => {
    await navigator.clipboard.writeText(url);
    showToast('복사되었습니다.', 'success');
  };

  return (
    <div css={QRURLContainer}>
      <p>공유 URL</p>

      <div css={URLContainer}>
        <div css={URLBox(theme)}>{url}</div>

        <div css={copyButton}>
          <BasicButton
            variant='secondary'
            padding='8px 0px'
            gap='0'
            fontSize={'12px'}
            height={'30px'}
            icon={<CopyIcon />}
            onClick={handleCopy}
          >
            복사
          </BasicButton>
        </div>
      </div>

      <p css={QRText(theme)}>
        이 링크를 공유하여 사용자가 피드백을 남길 수 있습니다.
      </p>
    </div>
  );
}
