import ProfileBox from './components/ProfileBox/ProfileBox';
import { settingsContainer } from './Settings.style';
import SettingListBox from './components/SettingListBox/SettingListBox';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import OutOutlineIcon from '@/components/icons/OutOutlineIcon';
import { useState } from 'react';

export default function Settings() {
  const [enableNotification, setEnableNotification] = useState(false);

  const handleNotificationToggle = () => {
    setEnableNotification(!enableNotification);
  };

  return (
    <div css={settingsContainer}>
      <ProfileBox name='우아한테크코스' id='woowacourse' />
      <SettingListBox
        icon={<BellOutlineIcon />}
        title='알림 설정'
        description='푸시 알림 받기 설정'
        rightElement={
          <BasicToggleButton
            isToggled={enableNotification}
            onClick={handleNotificationToggle}
            name='notification-toggle'
          />
        }
      />
      <SettingListBox
        icon={<OutOutlineIcon />}
        title='로그아웃'
        variant='danger'
        onClick={() => {}}
      />
    </div>
  );
}
