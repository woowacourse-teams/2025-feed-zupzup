import { useLocation, useNavigate } from 'react-router-dom';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useKeyboardDetection } from '@/hooks/useKeyboardDetection';
import {
  bottomNavStyle,
  navItemStyle,
  navTextStyle,
} from './BottomNavigation.style';
import HomeIcon from '../icons/HomeIcon';
import SettingIcon from '../icons/SettingIcon';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';

export default function BottomNavigation() {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useAppTheme();
  const isKeyboardOpen = useKeyboardDetection();

  if (!LAYOUT_CONFIGS[location.pathname]?.bottomNav.show) {
    return null;
  }

  const isHomeActive = location.pathname === '/admin-home';
  const isSettingsActive = location.pathname === '/admin-settings';

  return (
    <nav
      css={bottomNavStyle(theme)}
      className={isKeyboardOpen ? 'keyboard-open' : ''}
    >
      <div
        css={navItemStyle(theme, isHomeActive)}
        onClick={() => navigate('/admin-home')}
      >
        <HomeIcon
          color={
            isHomeActive ? theme.colors.purple[100] : theme.colors.gray[600]
          }
        />
        <span css={navTextStyle(theme, isHomeActive)}>홈</span>
      </div>

      <div
        css={navItemStyle(theme, isSettingsActive)}
        onClick={() => navigate('/admin-settings')}
      >
        <SettingIcon
          color={
            isSettingsActive ? theme.colors.purple[100] : theme.colors.gray[600]
          }
        />
        <span css={navTextStyle(theme, isSettingsActive)}>설정</span>
      </div>
    </nav>
  );
}
