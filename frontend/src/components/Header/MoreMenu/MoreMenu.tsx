import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';
import { useModalContext } from '@/contexts/useModal';
import QRModal from '@/domains/admin/components/QRModal/QRModal';
import CreateRoomModal from '@/domains/admin/CreateRoomModal/CreateRoomModal';

interface MoreMenuProps {
  closeMoreMenu: () => void;
}

export default function MoreMenu({ closeMoreMenu }: MoreMenuProps) {
  const { openModal, closeModal, isOpen } = useModalContext();

  const handleRoomInfoEditClick = () => {
    openModal(<CreateRoomModal isOpen={isOpen} onClose={closeModal} />);
    closeMoreMenu();
  };

  const handleShareClick = () => {
    openModal(<QRModal isOpen={isOpen} onClose={closeModal} />);
    closeMoreMenu();
  };

  const moreMenuList = [
    {
      icon: SmallSettingIcon,
      menu: '방정보 수정',
      onClick: handleRoomInfoEditClick,
    },
    { icon: ShareIcon, menu: 'QR/URL 공유', onClick: handleShareClick },
  ];

  return (
    <div css={moreMenuContainer}>
      {moreMenuList.map((item, index) => (
        <MoreMenuItem
          key={index}
          icon={<item.icon />}
          menu={item.menu}
          onClick={item.onClick}
        />
      ))}
    </div>
  );
}
