import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import QRImageSection from '@/domains/admin/components/QRModal/components/QRImageSection';
import QRUrlSection from '@/domains/admin/components/QRModal/components/QRUrlSection';

export default function QRModal() {
  return (
    <Modal isOpen={true} onClose={() => {}}>
      <QRImageSection dataUrl='' />
      <QRUrlSection url='https::naver.com/' />
      <BasicButton variant='secondary' padding={'8px 16px'} height={30}>
        취소
      </BasicButton>
    </Modal>
  );
}
