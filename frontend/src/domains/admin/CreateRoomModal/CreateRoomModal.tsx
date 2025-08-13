import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import { buttonContainer } from '@/domains/admin/CreateRoomModal/CreateRoomModal.styles';
import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import {
  roomModalContainer,
  roomModalTitle,
} from '@/domains/admin/CreateRoomModal/CreateRoomModal.styles';
import { useCategorySelection } from '@/domains/admin/CreateRoomModal/hooks/useCategorySelection';
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
  const [roomName, setRoomName] = useState('');

  const { selectedCategories, handleCategoryClick, handleCategoryTagClick } =
    useCategorySelection();

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <section css={roomModalContainer}>
        <p css={roomModalTitle}>새 피드백 방 만들기</p>
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
          방 만들기
        </BasicButton>
      </div>
    </Modal>
  );
}
