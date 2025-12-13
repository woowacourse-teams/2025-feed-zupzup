import Modal from '@/components/Modal/Modal';
import { modalWidth } from '@/components/Modal/Modal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';
import EditTab from './components/EditTab';
import DeleteTab from './components/DeleteTab';
import {
  modalTitle,
  tabContainer,
  tabButton,
} from '@/domains/admin/ManageRoomModal/ManageRoomModal.styles';

type TabType = 'edit' | 'delete';

interface ManageRoomModalProps {
  onClose: () => void;
}

export default function ManageRoomModal({ onClose }: ManageRoomModalProps) {
  const theme = useAppTheme();
  const [activeTab, setActiveTab] = useState<TabType>('edit');

  return (
    <Modal onClose={onClose} customCSS={modalWidth}>
      <p css={modalTitle(theme)}>방 수정/삭제하기</p>
      <div css={tabContainer(theme)}>
        <button
          css={tabButton(theme, activeTab === 'edit')}
          onClick={() => setActiveTab('edit')}
        >
          피드백 방 수정
        </button>
        <button
          css={tabButton(theme, activeTab === 'delete')}
          onClick={() => setActiveTab('delete')}
        >
          피드백 방 삭제
        </button>
      </div>

      {activeTab === 'edit' ? (
        <EditTab onClose={onClose} />
      ) : (
        <DeleteTab onClose={onClose} />
      )}
    </Modal>
  );
}
