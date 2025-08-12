import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import { buttonContainer } from '@/components/Modal/Modal.styles';
import { CategoryListType } from '@/constants/categoryList';
import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import {
  roomModalContainer,
  roomModalTitle,
} from '@/domains/admin/CreateRoomModal/CreateRoomModal.styles';
import { useState } from 'react';

interface CreateRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CreateRoomModal({
  isOpen,
  onClose,
}: CreateRoomModalProps) {
  const [roomName, setRoomName] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<
    { icon: string | React.ReactNode; category: CategoryListType }[]
  >([]);

  const handleCategoryTagClick = (category: CategoryListType) => {
    setSelectedCategories((prev) =>
      prev.filter((tag) => tag.category !== category)
    );
  };

  const handleCategorySelect = (
    icon: string | React.ReactNode,
    category: CategoryListType
  ) => {
    setSelectedCategories((prev) => {
      const isExists = prev.some((item) => item.category === category);

      if (isExists) {
        return prev.filter((item) => item.category !== category);
      }

      if (prev.length >= 4) {
        alert('카테고리는 최대 4개까지만 선택할 수 있습니다.');
        return prev;
      }

      return [...prev, { icon, category }];
    });
  };

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
          handleCategorySelect={handleCategorySelect}
        />
        <RoomCategoryTagList
          selectedCategories={selectedCategories}
          handleCategoryTagClick={handleCategoryTagClick}
        />
      </section>
      <div css={buttonContainer}>
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
