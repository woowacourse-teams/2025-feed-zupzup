import BasicButton from '@/components/BasicButton/BasicButton';
import Modal from '@/components/Modal/Modal';
import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import { useCategorySelection } from '@/domains/admin/CreateRoomModal/hooks/useCategorySelection';
import {
  editRoomModalContainer,
  editRoomModalTitle,
  buttonContainer,
  tabContainer,
  tabButton,
  deleteContent,
  deleteMessage,
} from '@/domains/admin/EditRoomModal/EditRoomModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState, useEffect } from 'react';
import RoomNameInput from '../components/RoomNameInput/RoomNameInput';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import useEditRoom from './hooks/useEditRoom';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useModalContext } from '@/contexts/useModal';
import AlertModal from '@/components/AlertModal/AlertModal';
import { modalWidth } from '@/components/Modal/Modal.styles';
import useDeleteOrganization from './hooks/useDeleteOrganization';

type TabType = 'edit' | 'delete';

interface EditRoomModalProps {
  onClose: () => void;
}

export default function EditRoomModal({ onClose }: EditRoomModalProps) {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { groupName, categories } = useOrganizationName({
    organizationId,
  });
  const { openModal, closeModal } = useModalContext();
  const { deleteOrganization, isDeleting } = useDeleteOrganization();

  const [activeTab, setActiveTab] = useState<TabType>('edit');
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
      onClose();
      openModal(<AlertModal onClose={closeModal} title='방 수정 완료' />);
    } catch (e) {
      console.error(e);
      return;
    }
  };

  const handleDeleteButton = async () => {
    await deleteOrganization();
    onClose();
  };

  return (
    <Modal onClose={onClose} customCSS={modalWidth}>
      <p css={editRoomModalTitle}>피드백 방 수정하기</p>
      <div css={tabContainer}>
        <button
          css={tabButton(activeTab === 'edit')}
          onClick={() => setActiveTab('edit')}
        >
          피드백 방 수정
        </button>
        <button
          css={tabButton(activeTab === 'delete')}
          onClick={() => setActiveTab('delete')}
        >
          피드백 방 삭제
        </button>
      </div>

      {activeTab === 'edit' ? (
        <>
          <section css={editRoomModalContainer}>
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
      ) : (
        <>
          <div css={deleteContent}>
            <p css={deleteMessage}>
              삭제한 방은 되돌릴 수 없습니다.{'\n'}정말로 방을 삭제하시겠습니까?
            </p>
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
              variant='danger'
              width={'48%'}
              padding={'8px 8px'}
              height={'40px'}
              fontSize={'16px'}
              onClick={handleDeleteButton}
              disabled={isDeleting}
            >
              {isDeleting ? '삭제 중...' : '삭제하기'}
            </BasicButton>
          </div>
        </>
      )}
    </Modal>
  );
}
