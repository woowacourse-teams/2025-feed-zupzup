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
import { useState } from 'react';

interface EditRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function EditRoomModal({ isOpen, onClose }: EditRoomModalProps) {
  const theme = useAppTheme();
  const [roomName, setRoomName] = useState('');

  const { selectedCategories, handleCategoryClick, handleCategoryTagClick } =
    useCategorySelection();

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <section css={editRoomModalContainer}>
        <p css={editRoomModalTitle}>피드백 방 수정하기</p>
        <p>방 이름</p>
        <input
          type='text'
          value={roomName}
          onChange={(e) => setRoomName(e.target.value)}
          placeholder='방 이름을 입력하세요'
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
        <BasicButton variant='secondary' width={'48%'}>
          취소
        </BasicButton>
        <BasicButton variant='primary' width={'48%'}>
          수정하기
        </BasicButton>
      </div>
    </Modal>
  );
}
