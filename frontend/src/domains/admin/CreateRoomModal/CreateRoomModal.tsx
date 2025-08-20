import { CategoryType } from '@/analytics/types';
import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import RoomNameInput from '@/domains/admin/components/RoomNameInput/RoomNameInput';
import {
  buttonContainer,
  roomModalContainer,
  roomModalTitle,
} from '@/domains/admin/CreateRoomModal/CreateRoomModal.styles';
import {
  SelectedCategoryItem,
  useCategorySelection,
} from '@/domains/admin/CreateRoomModal/hooks/useCategorySelection';
import useCreateOrganization from '@/domains/admin/CreateRoomModal/hooks/useCreateOrganization';
import useSubmitDisabled from '@/domains/admin/CreateRoomModal/hooks/useSubmitDisabled';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';

interface CreateRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CreateRoomModal({
  isOpen,
  onClose,
}: CreateRoomModalProps) {
  const theme = useAppTheme();
  const [organizationName, setOrganizationName] = useState('');

  const { selectedCategories, handleCategoryClick, handleCategoryTagClick } =
    useCategorySelection();

  const { createRoom, isPending } = useCreateOrganization({
    onClose,
  });

  const { disabled } = useSubmitDisabled({
    organizationName,
    selectedCategories,
  });

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <section css={roomModalContainer}>
        <p css={roomModalTitle}>새 피드백 방 만들기</p>

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
          onClick={onClose}
        >
          취소
        </BasicButton>
        <BasicButton
          variant={disabled ? 'disabled' : 'primary'}
          width={'48%'}
          padding={'8px 8px'}
          height={'40px'}
          fontSize={'16px'}
          onClick={() =>
            createRoom({
              organizationName: organizationName,
              categories: selectedCategories.map(
                (category: SelectedCategoryItem) =>
                  category.category as CategoryType
              ),
            })
          }
          disabled={disabled}
        >
          {isPending ? '제출 중입니다' : '방 만들기'}
        </BasicButton>
      </div>
    </Modal>
  );
}
