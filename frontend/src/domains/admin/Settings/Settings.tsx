import ProfileBox from './components/ProfileBox/ProfileBox';
import { settingsContainer } from './Settings.style';
import SettingListBox from './components/SettingListBox/SettingListBox';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';

export default function Settings() {
  return (
    <div css={settingsContainer}>
      <ProfileBox name='우아한테크코스' id='woowacourse' />
      <SettingListBox
        icon={<BellOutlineIcon />}
        title='알림 설정'
        description='푸시 알림 받기 설정'
        rightElement={
          <BasicToggleButton
            isToggled={true}
            onClick={() => console.log('토글 클릭됨')}
            name='notification-toggle'
          />
        }
      />
    </div>
  );
}
