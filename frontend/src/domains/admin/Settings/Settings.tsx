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

export default function Settings() {
  const [modalState, setModalState] = useState<'logout' | null>(null);
  const {
    isToggleEnabled,
    updateNotificationSetting,
    isLoading: isNotificationLoading,
  } = useNotificationSetting();

  const { handleLogout, isLoading: isLogoutLoading } = useLogout();

  const handleLogoutConfirm = async () => {
    await handleLogout();
    setModalState(null);
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
            onClick={() => updateNotificationSetting(!isToggleEnabled)}
            name='notification-toggle'
            disabled={isNotificationLoading}
          />
        }
      />
      <SettingListBox
        icon={<OutOutlineIcon />}
        title='로그아웃'
        variant='danger'
        onClick={() => setModalState('logout')}
      />
      <ConfirmModal
        title='로그아웃'
        message='로그아웃 하시겠습니까?'
        isOpen={modalState === 'logout'}
        onClose={() => !isLogoutLoading && setModalState(null)}
        onConfirm={handleLogoutConfirm}
      />
    </div>
  );
}
