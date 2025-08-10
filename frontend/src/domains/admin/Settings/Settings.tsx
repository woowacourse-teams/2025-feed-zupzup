import ListBox from './components/ListBox/ListBox';
import ProfileBox from './components/ProfileBox/ProfileBox';
import { settingsContainer } from './Settings.style';

export default function Settings() {
  return (
    <div css={settingsContainer}>
      <ProfileBox />
      <ListBox />
    </div>
  );
}
