import { useLocation, useNavigate } from 'react-router-dom';
import { useAppTheme } from '@/hooks/useAppTheme';
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

  const currentPath = location.pathname;

  if (!LAYOUT_CONFIGS[currentPath]?.bottomNav.show) {
    return null;
  }

  const isHomeActive = location.pathname === '/admin';
  const isSettingsActive = location.pathname === '/settings';

  return (
    <nav css={bottomNavStyle(theme)}>
      <div
        css={navItemStyle(theme, isHomeActive)}
        onClick={() => navigate('/admin')}
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
        onClick={() => navigate('/settings')}
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
