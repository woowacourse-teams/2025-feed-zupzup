import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import { useCategorySelection } from '@/domains/admin/CreateRoomModal/hooks/useCategorySelection';
import {
  editRoomModalContainer,
  editRoomModalTitle,
  buttonContainer,
} from '@/domains/admin/EditRoomModal/EditRoomModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState, useEffect } from 'react';
import RoomNameInput from '../components/RoomNameInput/RoomNameInput';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import useEditRoom from './hooks/useEditRooom';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';

interface EditRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function EditRoomModal({ isOpen, onClose }: EditRoomModalProps) {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { groupName, categories } = useOrganizationName({
    organizationId,
  });

  const [organizationName, setOrganizationName] = useState('');

  const { selectedCategories, handleCategoryClick, handleCategoryTagClick } =
    useCategorySelection({
      initialCategories: categories,
    });

  useEffect(() => {
    if (groupName) {
      setOrganizationName(groupName);
    }
  }, [groupName]);

  const { handleRoomEditButton, isLoading } = useEditRoom({
    organizationName,
    categories: selectedCategories.map((category) => category.category),
  });

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <section css={editRoomModalContainer}>
        <p css={editRoomModalTitle}>피드백 방 수정하기</p>
        <RoomNameInput
          roomName={organizationName}
          onChange={(e) => {
            setOrganizationName(e.target.value);
          }}
        />
        <RoomCategoryList
          selectedCategories={selectedCategories}
          handleCategoryClick={handleCategoryClick}
        />
        <RoomCategoryTagList
          selectedCategories={selectedCategories}
          handleCategoryTagClick={handleCategoryTagClick}
        />
      </section>
      <div css={buttonContainer(theme)}>
        <BasicButton
          variant='secondary'
          width={'48%'}
          padding={'8px 8px'}
          height={'40px'}
          fontSize={'16px'}
          disabled={isLoading}
        >
          취소
        </BasicButton>
        <BasicButton
          variant='secondary'
          width={'48%'}
          padding={'8px 8px'}
          height={'40px'}
          fontSize={'16px'}
          onClick={() => handleRoomEditButton()}
          disabled={isLoading}
        >
          수정하기
        </BasicButton>
      </div>
    </Modal>
  );
}
