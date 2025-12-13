import useDownloadFeedbacks from '@/components/Header/hooks/useDownloadFeedbacks';
import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ExternalIcon from '@/components/icons/External';
import FileDownloadIcon from '@/components/icons/FileDownloadIcon';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';
import { useModalContext } from '@/contexts/useModal';
import { useToast } from '@/contexts/useToast';
import QRModal from '@/domains/admin/components/QRModal/QRModal';
import ManageRoomModal from '@/domains/admin/ManageRoomModal/ManageRoomModal';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useNavigate } from 'react-router-dom';

interface MoreMenuProps {
  closeMoreMenu: () => void;
}

export default function MoreMenu({ closeMoreMenu }: MoreMenuProps) {
  const { openModal, closeModal } = useModalContext();
  const { organizationId } = useOrganizationId();
  const { refetch, isFetching } = useDownloadFeedbacks(organizationId);
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleRoomInfoEditClick = () => {
    openModal(<ManageRoomModal onClose={closeModal} />);
    closeMoreMenu();
  };

  const handleShareClick = () => {
    openModal(<QRModal onClose={closeModal} />);
    closeMoreMenu();
  };

  const downloadFeedbacksFile = async () => {
    showToast(
      '피드백 데이터를 추출 중입니다. 잠시만 기다려주세요.',
      'origin',
      2000
    );

    closeMoreMenu();
    await refetch();
  };

  const handleCustomerPageClick = () => {
    navigate(`/${organizationId}/submit`);
    closeMoreMenu();
  };

  const moreMenuList = [
    {
      icon: <SmallSettingIcon />,
      menu: '방정보 수정/삭제',
      onClick: handleRoomInfoEditClick,
    },
    { icon: <ShareIcon />, menu: 'QR/URL 공유', onClick: handleShareClick },
    {
      icon: <FileDownloadIcon />,
      menu: '피드백 추출',
      onClick: downloadFeedbacksFile,
    },
    {
      icon: <ExternalIcon />,
      menu: '고객 페이지로 이동',
      onClick: handleCustomerPageClick,
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
          disabled={isFetching && item.menu === '피드백 추출'}
        />
      ))}
    </div>
  );
}
