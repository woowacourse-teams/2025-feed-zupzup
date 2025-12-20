import { FileDownloadType } from '@/apis/adminFeedback.api';
import useStartDownloadFeedbacks from '@/components/Header/hooks/useStartDownloadFeedbacks';
import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ProgressMenuItem from '@/components/Header/ProgressMenuItem/ProgressMenuItem';
import ExternalIcon from '@/components/icons/External';
import FileDownloadIcon from '@/components/icons/FileDownloadIcon';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';
import { useModalContext } from '@/contexts/useModal';
import { useToast } from '@/contexts/useToast';
import QRModal from '@/domains/admin/components/QRModal/QRModal';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useNavigate } from 'react-router-dom';
import ManageRoomModal from '../../../domains/admin/ManageRoomModal/ManageRoomModal';

interface MoreMenuProps {
  closeMoreMenu: () => void;
  feedbackDownloadStatus?: FileDownloadType | undefined;
  setJobId: React.Dispatch<React.SetStateAction<string>>;
}

export default function MoreMenu({
  closeMoreMenu,
  setJobId,
  feedbackDownloadStatus,
}: MoreMenuProps) {
  const { openModal, closeModal } = useModalContext();
  const { organizationId } = useOrganizationId();

  const { mutateAsync: startDownloadFeedbacks } = useStartDownloadFeedbacks({
    organizationId,
    setJobId,
  });
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

    try {
      await startDownloadFeedbacks();
    } catch {
      showToast(
        '피드백 데이터 추출에 실패했습니다. 다시 시도해주세요.',
        'error',
        3000
      );
      return;
    } finally {
      closeMoreMenu();
    }
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
      icon: <ExternalIcon />,
      menu: '고객 페이지로 이동',
      onClick: handleCustomerPageClick,
    },
  ];

  const downloadMoreMenuList = [
    {
      icon: <FileDownloadIcon />,
      menu: '피드백 추출',
      onClick: downloadFeedbacksFile,
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

      {downloadMoreMenuList.map((item, index) => (
        <ProgressMenuItem
          key={index}
          icon={item.icon}
          menu={item.menu}
          onClick={item.onClick}
          progress={feedbackDownloadStatus?.progress || 0}
          disabled={feedbackDownloadStatus?.jobStatus === 'PROCESSING'}
        />
      ))}
    </div>
  );
}
