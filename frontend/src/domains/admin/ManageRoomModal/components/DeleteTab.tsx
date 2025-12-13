import BasicButton from '@/components/BasicButton/BasicButton';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';
import useDeleteOrganization from '../hooks/useDeleteOrganization';
import {
  deleteContent,
  deleteWarningText,
  deleteItemsList,
  deleteAgreementLabel,
  deleteCheckbox,
  buttonContainer,
} from '@/domains/admin/ManageRoomModal/ManageRoomModal.styles';

interface DeleteTabProps {
  onClose: () => void;
}

export default function DeleteTab({ onClose }: DeleteTabProps) {
  const theme = useAppTheme();
  const { deleteOrganization, isDeleting } = useDeleteOrganization();
  const [isDeleteChecked, setIsDeleteChecked] = useState(false);

  const handleDeleteButton = async () => {
    await deleteOrganization();
    onClose();
  };

  return (
    <>
      <div css={deleteContent}>
        <p css={deleteWarningText(theme)}>
          삭제 시 정보가 삭제되며 복구 불가합니다.
        </p>
        <ul css={deleteItemsList(theme)}>
          <li>피드백 방 정보</li>
          <li>받은 모든 피드백 데이터</li>
        </ul>
        <label css={deleteAgreementLabel(theme)}>
          <input
            type='checkbox'
            checked={isDeleteChecked}
            onChange={(e) => setIsDeleteChecked(e.target.checked)}
            css={deleteCheckbox}
          />
          <span>위 내용을 모두 확인했고, 삭제하는 것에 동의합니다.</span>
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
