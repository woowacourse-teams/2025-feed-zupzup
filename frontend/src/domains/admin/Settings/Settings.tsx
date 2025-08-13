import ProfileBox from './components/ProfileBox/ProfileBox';
import { settingsContainer } from './Settings.style';
import SettingListBox from './components/SettingListBox/SettingListBox';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import OutOutlineIcon from '@/components/icons/OutOutlineIcon';
import { useState } from 'react';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { useNotificationSetting } from './hooks/useNotificationSetting';
import { useLogout } from './hooks/useLogout';

interface ModalState {
  type: 'logout' | null;
}

export default function Settings() {
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const {
    isToggleEnabled,
    updateNotificationSetting,
    isLoading: isNotificationLoading,
    fcmStatus,
    sendTestNotification,
    clearError,
  } = useNotificationSetting();

  const { handleLogout } = useLogout();

  const handleLogoutConfirm = async () => {
    await handleLogout();
    setModalState({ type: null });
  };

  const handleNotificationToggle = async () => {
    clearError();
    await updateNotificationSetting(!isToggleEnabled);
  };

  const handleTestNotification = async () => {
    if (sendTestNotification) {
      try {
        setTimeout(async () => {
          try {
            await sendTestNotification();

            if (
              'Notification' in window &&
              Notification.permission === 'granted'
            ) {
              new Notification('테스트 알림 🔔', {
                body: '10초 후에 발송된 테스트 알림입니다!',
                icon: '/favicon.ico',
                badge: '/favicon.ico',
                tag: 'test-notification',
              });
            }
          } catch (error) {
            console.error('테스트 알림 발송 오류:', error);
          }
        }, 10000);

        alert('10초 후에 테스트 알림이 발송됩니다! ⏰');
      } catch (error) {
        console.error('테스트 알림 발송 오류:', error);
        alert('테스트 알림 발송에 실패했습니다.');
      }
    }
  };

  //TODO: 아래 사용자 이름과 ID는 이후 전역상태 참조해서 추가 구현해야함.

  return (
    <div css={settingsContainer}>
      <ProfileBox name='우아한테크코스' id='woowacourse' />

      <SettingListBox
        icon={<BellOutlineIcon />}
        title='알림 설정'
        description='푸시 알림 받기 설정'
        rightElement={
          <BasicToggleButton
            isToggled={isToggleEnabled}
            onClick={handleNotificationToggle}
            name='notification-toggle'
            disabled={isNotificationLoading || !fcmStatus.isSupported}
          />
        }
      />

      {/* 개발 환경에서만 테스트 버튼 표시 */}
      {process.env.NODE_ENV === 'development' && sendTestNotification && (
        <SettingListBox
          icon={<BellOutlineIcon />}
          title='테스트 알림 발송'
          description='개발용: 테스트 알림을 즉시 발송합니다'
          onClick={handleTestNotification}
        />
      )}

      <SettingListBox
        icon={<OutOutlineIcon />}
        title='로그아웃'
        variant='danger'
        onClick={() => setModalState({ type: 'logout' })}
      />

      {modalState.type === 'logout' && (
        <ConfirmModal
          title='로그아웃'
          message='로그아웃 하시겠습니까?'
          onClose={() => setModalState({ type: null })}
          onConfirm={handleLogoutConfirm}
        />
      )}
    </div>
  );
}
