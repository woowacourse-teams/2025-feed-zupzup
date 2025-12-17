import BasicButton from '@/components/BasicButton/BasicButton';
import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import RoomNameInput from '@/domains/admin/components/RoomNameInput/RoomNameInput';
import { useCategorySelection } from '@/domains/admin/CreateRoomModal/hooks/useCategorySelection';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useModalContext } from '@/contexts/useModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState, useEffect } from 'react';
import useEditRoom from '../hooks/useEditRoom';
import { editTabContainer } from './EditTab.styles';
import { buttonContainer } from '../ManageRoomModal.styles';

interface EditTabProps {
  onClose: () => void;
}

export default function EditTab({ onClose }: EditTabProps) {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { groupName, categories } = useOrganizationName({ organizationId });
  const { openModal, closeModal } = useModalContext();

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

  const { editRoom, isLoading } = useEditRoom({
    organizationName,
    categories: selectedCategories.map((category) => category.category),
  });

  const handleRoomEditButton = async () => {
    try {
      await editRoom();
    } catch {
      return;
    }
    onClose();
    openModal(<AlertModal onClose={closeModal} title='방 수정 완료' />);
  };

  return (
    <>
      <section css={editTabContainer}>
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
          onClick={handleRoomEditButton}
          disabled={isLoading}
        >
          수정하기
        </BasicButton>
      </div>
    </>
  );
}
