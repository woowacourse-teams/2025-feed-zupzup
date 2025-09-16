import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import QRImageSection from '@/domains/admin/components/QRModal/components/QRImageSection/QRImageSection';
import QRUrlSection from '@/domains/admin/components/QRModal/components/QRUrlSection/QRUrlSection';
import { useQRCode } from './hooks/useQRCode';

interface QRModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function QRModal({ isOpen, onClose }: QRModalProps) {
  const { data, isLoading } = useQRCode();

  if (isLoading) {
    return (
      <Modal isOpen={isOpen} onClose={onClose}>
        <div>로딩 중...</div>
      </Modal>
    );
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <QRImageSection url={data?.imageUrl} />
      <QRUrlSection url={data?.siteUrl} />
      <BasicButton
        variant='secondary'
        padding={'8px 16px'}
        height={'30px'}
        onClick={onClose}
      >
        취소
      </BasicButton>
    </Modal>
  );
}
