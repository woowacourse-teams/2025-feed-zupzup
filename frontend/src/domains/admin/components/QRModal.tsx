import BasicButton from '@/components/BasicButton/BasicButton';
import CopyIcon from '@/components/icons/CopyIcon';
import DownloadIcon from '@/components/icons/DownloadIcon';
import Modal from '@/components/Modal/Modal';
import {
  copyButton,
  QRCodeContainer,
  QRImageContainer,
  QRText,
  QRTitle,
  QRURLContainer,
  URLBox,
  URLContainer,
} from '@/domains/admin/components/QRModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';

export default function QRModal() {
  const theme = useAppTheme();

  return (
    <Modal isOpen={true} onClose={() => {}}>
      <div css={QRCodeContainer}>
        <p css={QRTitle(theme)}>QR 코드 & URL 공유</p>
        <div css={QRImageContainer}>
          <img
            src='data:image/png;base64,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'
            alt=''
          />
        </div>
        <p css={copyButton}>
          위 코드를 스캔하여 피드백 페이지로 이동할 수 있습니다.
        </p>
        <BasicButton
          variant='secondary'
          padding={'8px 8px'}
          fontSize={12}
          height={30}
          width={'70%'}
          icon={<DownloadIcon />}
        >
          QR 코드 다운로드
        </BasicButton>
      </div>
      <div css={QRURLContainer}>
        <p>공유 URL</p>
        <div css={URLContainer}>
          <div css={URLBox(theme)}>
            https://feedback.example.com/room/abc123
          </div>
          <div>
            <BasicButton
              variant='secondary'
              padding={'8px 0px'}
              gap={'0'}
              fontSize={12}
              height={30}
              icon={<CopyIcon />}
            >
              복사
            </BasicButton>
          </div>
        </div>
        <p css={QRText(theme)}>
          이 링크를 공유하여 사용자가 피드백을 남길 수 있습니다.
        </p>
      </div>
      <BasicButton variant='secondary' padding={'8px 16px'} height={30}>
        취소
      </BasicButton>
    </Modal>
  );
}
