import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import QRImageSection from '@/domains/admin/components/QRModal/components/QRImageSection/QRImageSection';
import QRUrlSection from '@/domains/admin/components/QRModal/components/QRUrlSection/QRUrlSection';

interface QRModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function QRModal({ isOpen, onClose }: QRModalProps) {
  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <QRImageSection url='' />
      <QRUrlSection url='https::naver.com/' />
      <BasicButton variant='secondary' padding={'8px 16px'} height={30}>
        취소
      </BasicButton>
    </Modal>
  );
}
