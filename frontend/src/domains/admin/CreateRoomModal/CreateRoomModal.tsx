import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import { modalWidth } from '@/components/Modal/Modal.styles';
import { CategoryListType } from '@/constants/categoryList';
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
import { useAppTheme } from '@/hooks/useAppTheme';
import { AdminAuthData } from '@/types/adminAuth';
import { getLocalStorage } from '@/utils/localStorage';
import { useMemo, useState } from 'react';

interface CreateRoomModalProps {
  onClose: () => void;
}

export default function CreateRoomModal({ onClose }: CreateRoomModalProps) {
  const theme = useAppTheme();
  const [organizationName, setOrganizationName] = useState('');

  const { selectedCategories, handleCategoryClick, handleCategoryTagClick } =
    useCategorySelection();

  const adminName =
    getLocalStorage<AdminAuthData>('auth')?.adminName || '관리자1';

  const { createRoom, isPending } = useCreateOrganization({
    onClose,
    name: adminName,
  });

  const isDisabled = useMemo(() => {
    return !organizationName.trim() || selectedCategories.length === 0;
  }, [organizationName, selectedCategories.length]);

  return (
    <Modal onClose={onClose} customCSS={modalWidth}>
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
          variant={isDisabled ? 'disabled' : 'primary'}
          width={'48%'}
          padding={'8px 8px'}
          height={'40px'}
          fontSize={'16px'}
          onClick={() =>
            createRoom({
              organizationName: organizationName,
              categories: selectedCategories.map(
                (category: SelectedCategoryItem) =>
                  category.category as CategoryListType
              ),
            })
          }
          disabled={isDisabled}
        >
          {isPending ? '제출 중입니다' : '방 만들기'}
        </BasicButton>
      </div>
    </Modal>
  );
}
