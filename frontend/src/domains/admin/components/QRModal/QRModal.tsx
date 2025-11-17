import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import QRImageSection from '@/domains/admin/components/QRModal/components/QRImageSection/QRImageSection';
import QRUrlSection from '@/domains/admin/components/QRModal/components/QRUrlSection/QRUrlSection';
import QRModalSkeleton from '@/domains/admin/components/QRModal/components/QRModalSkeleton/QRModalSkeleton';
import { useQRCode } from './hooks/useQRCode';
import { modalWidth } from '@/components/Modal/Modal.styles';

interface QRModalProps {
  onClose: () => void;
}

export default function QRModal({ onClose }: QRModalProps) {
  const { data, isLoading } = useQRCode();

  if (isLoading) {
    return (
      <Modal onClose={onClose} customCSS={modalWidth}>
        <QRModalSkeleton />
      </Modal>
    );
  }

  return (
    <Modal onClose={onClose} customCSS={modalWidth}>
      <QRImageSection url={data?.imageUrl || ''} />
      <QRUrlSection url={data?.siteUrl || ''} />
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
