import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import { useModalContext } from '@/contexts/useModal';
import QRModal from '@/domains/admin/components/QRModal/QRModal';
import EditRoomModal from '@/domains/admin/EditRoomModal/EditRoomModal';

interface MoreMenuProps {
  closeMoreMenu: () => void;
}

export default function MoreMenu({ closeMoreMenu }: MoreMenuProps) {
  const { openModal, closeModal, isOpen } = useModalContext();

  const handleRoomInfoEditClick = () => {
    openModal(<EditRoomModal isOpen={isOpen} onClose={closeModal} />);
    closeMoreMenu();
  };

  const handleShareClick = () => {
    openModal(<QRModal isOpen={isOpen} onClose={closeModal} />);
    closeMoreMenu();
  };

  const handleDeleteClick = () => {
    openModal(
      <ConfirmModal
        isOpen={isOpen}
        onClose={closeModal}
        title='방 삭제 확인'
        message='정말로 방을 삭제하시겠습니까?'
        onConfirm={() => {
          console.log('방 삭제');
        }}
      />
    );
    closeMoreMenu();
  };

  const moreMenuList = [
    {
      icon: <SmallSettingIcon />,
      menu: '방정보 수정',
      onClick: handleRoomInfoEditClick,
    },
    { icon: <ShareIcon />, menu: 'QR/URL 공유', onClick: handleShareClick },
    {
      icon: <TrashCanIcon color='#222222' />,
      menu: '방 삭제',
      onClick: handleDeleteClick,
    },
  ];

  return (
    <div css={moreMenuContainer}>
      {moreMenuList.map((item, index) => (
        <MoreMenuItem
          key={index}
          icon={item.icon}
          menu={item.menu}
          onClick={item.onClick}
        />
      ))}
    </div>
  );
}
