import { SEO } from '@/components/SEO/SEO';
import { useState } from 'react';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import OutOutlineIcon from '@/components/icons/OutOutlineIcon';
import SendIcon from '@/components/icons/SendIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import ProfileBox from './components/ProfileBox/ProfileBox';
import SettingListBox from './components/SettingListBox/SettingListBox';
import WithdrawModal from './components/WithdrawModal/WithdrawModal';
import LogoutModal from './components/LogoutModal/LogoutModal';
import { useNotificationSettingsPage } from './hooks/useNotificationSettingsPage';
import { settingsContainer, withdrawSettingListBox } from './Settings.style';
import useAdminAuth from '@/domains/admin/Settings/hooks/useAdminAuth';
import { usePWAPrompt } from '@/contexts/usePWAPrompt';

declare global {
  interface Window {
    ChannelIO?: {
      (...args: unknown[]): void;
      showMessenger?: () => void;
    };
  }
}

type ModalState = { type: 'logout' } | { type: 'withdraw' } | { type: null };

export default function Settings() {
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const {
    isToggleEnabled,
    updateNotificationSetting,
    isLoading,
    fcmStatus,
    needsPWAInstall,
  } = useNotificationSettingsPage();
  const { adminAuth, isLoading: isAdminAuthLoading } = useAdminAuth();
  const { showPrompt } = usePWAPrompt();

  const closeModal = () => {
    setModalState({ type: null });
  };

  const handleToggleClick = () => {
    updateNotificationSetting(!isToggleEnabled);
  };

  const handleCustomerServiceClick = () => {
    if (window.ChannelIO) {
      window.ChannelIO('showMessenger');
    }
  };

  const handleNotificationSettingClick = () => {
    if (needsPWAInstall) {
      showPrompt();
    }
  };

  const getNotificationMessage = () => {
    if (needsPWAInstall) {
      return '알림을 받기위해 홈 화면에 앱을 추가해주세요. 터치하면 설치 화면이 나옵니다.';
    }
    return '푸시 알림 받기 설정';
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
          description={getNotificationMessage()}
          {...(needsPWAInstall && { onClick: handleNotificationSettingClick })}
          rightElement={
            needsPWAInstall ? undefined : (
              <BasicToggleButton
                isToggled={isToggleEnabled}
                onClick={handleToggleClick}
                name='notification-toggle'
                disabled={isLoading || !fcmStatus.isSupported}
              />
            )
          }
        />

        <SettingListBox
          icon={<SendIcon />}
          title='고객센터'
          description='문의사항을 채널톡으로 전달하세요'
          onClick={handleCustomerServiceClick}
        />

        <SettingListBox
          icon={<OutOutlineIcon />}
          title='로그아웃'
          onClick={() => setModalState({ type: 'logout' })}
        />

        <div css={withdrawSettingListBox}>
          <SettingListBox
            icon={<TrashCanIcon />}
            title='회원탈퇴'
            variant='danger'
            onClick={() => setModalState({ type: 'withdraw' })}
          />
        </div>

        {modalState.type === 'logout' && <LogoutModal onClose={closeModal} />}

        {modalState.type === 'withdraw' && (
          <WithdrawModal onClose={closeModal} />
        )}
      </div>
    </>
  );
}
