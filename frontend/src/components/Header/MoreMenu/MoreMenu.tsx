import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import QRModal from '@/domains/admin/components/QRModal/QRModal';
import EditRoomModal from '@/domains/admin/EditRoomModal/EditRoomModal';
import useDeleteOrganization from '@/domains/admin/EditRoomModal/hooks/useDeleteOrganization';
import { useModalActions } from '@/stores/Modal/useModal';

interface MoreMenuProps {
  closeMoreMenu: () => void;
}

export default function MoreMenu({ closeMoreMenu }: MoreMenuProps) {
  const { openModal, closeModal } = useModalActions();
  const { deleteOrganization, isDeleting } = useDeleteOrganization();

  const handleRoomInfoEditClick = () => {
    openModal(<EditRoomModal onClose={closeModal} />);
    closeMoreMenu();
  };

  const handleShareClick = () => {
    openModal(<QRModal onClose={closeModal} />);
    closeMoreMenu();
  };

  const handleDeleteClick = () => {
    openModal(
      <ConfirmModal
        onClose={closeModal}
        title='방 삭제 확인'
        message={
          isDeleting
            ? '삭제 중입니다.'
            : '삭제한 방은 되돌릴 수 없습니다. \n정말로 방을 삭제하시겠습니까?'
        }
        onConfirm={deleteOrganization}
        disabled={isDeleting}
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
