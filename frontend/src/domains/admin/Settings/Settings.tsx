import { SEO } from '@/components/SEO/SEO';
import { useState } from 'react';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import OutOutlineIcon from '@/components/icons/OutOutlineIcon';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import ProfileBox from './components/ProfileBox/ProfileBox';
import SettingListBox from './components/SettingListBox/SettingListBox';
import { useLogout } from './hooks/useLogout';
import { useNotificationSettingsPage } from './hooks/useNotificationSettingsPage';
import { settingsContainer } from './Settings.style';
import useAdminAuth from '@/domains/admin/Settings/hooks/useAdminAuth';

type ModalState = { type: 'logout' } | { type: null };

export default function Settings() {
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const { isToggleEnabled, updateNotificationSetting, isLoading, fcmStatus } =
    useNotificationSettingsPage();
  const { adminAuth, isLoading: isAdminAuthLoading } = useAdminAuth();
  const { handleLogout } = useLogout();

  const closeModal = () => {
    setModalState({ type: null });
  };

  const handleToggleClick = () => {
    updateNotificationSetting(!isToggleEnabled);
  };

  return (
    <>
      <SEO
        title='설정'
        description='알림 설정 및 계정 관리'
        keywords='설정, 알림, 계정, 관리, settings'
      />
      <div css={settingsContainer}>
        <ProfileBox
          isLoading={isAdminAuthLoading}
          name={adminAuth?.data.adminName || ''}
          id={adminAuth?.data.loginId || ''}
        />

        <SettingListBox
          icon={<BellOutlineIcon />}
          title='알림 설정'
          description='푸시 알림 받기 설정'
          rightElement={
            <BasicToggleButton
              isToggled={isToggleEnabled}
              onClick={handleToggleClick}
              name='notification-toggle'
              disabled={isLoading || !fcmStatus.isSupported}
            />
          }
        />

        <SettingListBox
          icon={<OutOutlineIcon />}
          title='로그아웃'
          variant='danger'
          onClick={() => setModalState({ type: 'logout' })}
        />

        {modalState.type === 'logout' && (
          <ConfirmModal
            title='로그아웃'
            message='정말 로그아웃하시겠습니까?'
            onConfirm={handleLogout}
            onClose={closeModal}
          />
        )}
      </div>
    </>
  );
}
