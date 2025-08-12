import BasicButton from '@/components/BasicButton/BasicButton';
import CopyIcon from '@/components/icons/CopyIcon';
import {
  copyButton,
  QRText,
  QRURLContainer,
  URLBox,
  URLContainer,
} from '@/domains/admin/components/QRModal/QRModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

type QRUrlSectionProps = {
  url: string;
  helpText?: string;
};

export default function QRUrlSection({ url }: QRUrlSectionProps) {
  const theme = useAppTheme();

  const handleCopy = async () => {
    // 링크 복사 로직
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
            fontSize={12}
            height={30}
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
