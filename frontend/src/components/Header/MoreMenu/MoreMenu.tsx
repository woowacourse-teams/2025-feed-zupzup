import { FileDownloadType } from '@/apis/adminFeedback.api';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import useStartDownloadFeedbacks from '@/components/Header/hooks/useStartDownloadFeedbacks';
import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ProgressMenuItem from '@/components/Header/ProgressMenuItem/ProgressMenuItem';
import FileDownloadIcon from '@/components/icons/FileDownloadIcon';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import { useModalContext } from '@/contexts/useModal';
import { useToast } from '@/contexts/useToast';
import QRModal from '@/domains/admin/components/QRModal/QRModal';
import EditRoomModal from '@/domains/admin/EditRoomModal/EditRoomModal';
import useDeleteOrganization from '@/domains/admin/EditRoomModal/hooks/useDeleteOrganization';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';

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
  const { deleteOrganization, isDeleting } = useDeleteOrganization();
  const { showToast } = useToast();
  const { organizationId } = useOrganizationId();

  const { mutateAsync: startDownloadFeedbacks } = useStartDownloadFeedbacks({
    organizationId,
    setJobId,
  });

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

  const downloadFeedbacksFile = async () => {
    showToast(
      '피드백 데이터를 추출 중입니다. 잠시만 기다려주세요.',
      'origin',
      2000
    );

    try {
      const { data } = await startDownloadFeedbacks();
      setJobId(data.jobId);
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
