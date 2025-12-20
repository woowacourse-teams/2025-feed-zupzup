import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';
import useDeleteOrganization from '@/domains/admin/ManageRoomModal/hooks/useDeleteOrganization';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useToast } from '@/contexts/useToast';
import {
  deleteContent,
  warningText,
  roomInfoBox,
  roomInfoLabel,
  roomInfoName,
  deleteItemsTitle,
  deleteItemsList,
  deleteAgreementLabel,
  deleteCheckbox,
} from './DeleteTab.styles';
import { buttonContainer } from '../ManageRoomModal.styles';

interface DeleteTabProps {
  onClose: () => void;
}

export default function DeleteTab({ onClose }: DeleteTabProps) {
  const theme = useAppTheme();
  const { deleteOrganization, isDeleting } = useDeleteOrganization();
  const { organizationId } = useOrganizationId();
  const { groupName } = useOrganizationName({ organizationId });
  const [isDeleteChecked, setIsDeleteChecked] = useState(false);
  const { showToast } = useToast();

  const handleDeleteButton = async () => {
    try {
      await deleteOrganization();
    } catch {
      showToast('방 삭제에 실패했습니다.');
      return;
    }
    onClose();
  };

  return (
    <>
      <div css={deleteContent}>
        <p css={warningText(theme)}>
          삭제 시 모든 데이터가 영구적으로 삭제되며, 복구할 수 없습니다.
        </p>

        <div css={roomInfoBox(theme)}>
          <p css={roomInfoLabel(theme)}>삭제할 방</p>
          <p css={roomInfoName(theme)}>{groupName || '알 수 없음'}</p>
        </div>

        <div>
          <p css={deleteItemsTitle(theme)}>삭제되는 항목</p>
          <ul css={deleteItemsList(theme)}>
            <li>피드백 방 정보 및 설정</li>
            <li>받은 모든 피드백 데이터</li>
            <li>공유된 QR코드 및 URL</li>
          </ul>
        </div>

        <label css={deleteAgreementLabel(theme)}>
          <input
            type='checkbox'
            checked={isDeleteChecked}
            onChange={(e) => setIsDeleteChecked(e.target.checked)}
            css={deleteCheckbox}
          />
          <span>위 내용을 확인했으며, 삭제에 동의합니다.</span>
        </label>
      </div>

      <div css={buttonContainer(theme)}>
        <BasicButton
          variant='secondary'
          width={'48%'}
          padding={'8px 8px'}
          height={'40px'}
          fontSize={'16px'}
          disabled={isDeleting}
          onClick={onClose}
        >
          취소
        </BasicButton>
        <BasicButton
          variant='primary'
          width={'48%'}
          padding={'8px 8px'}
          height={'40px'}
          fontSize={'16px'}
          onClick={handleDeleteButton}
          disabled={!isDeleteChecked || isDeleting}
        >
          {isDeleting ? '삭제 중...' : '삭제하기'}
        </BasicButton>
      </div>
    </>
  );
}
