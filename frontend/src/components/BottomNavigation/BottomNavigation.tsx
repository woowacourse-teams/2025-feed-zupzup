import { useLocation, useNavigate } from 'react-router-dom';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useKeyboardDetection } from '@/hooks/useKeyboardDetection';
import {
  bottomNavStyle,
  navItemStyle,
  navTextStyle,
} from './BottomNavigation.style';
import { LAYOUT_CONFIGS } from '@/constants/layoutConfig';
import { NAVIGATION_ITEMS, NavigationItem } from './navigationItems';

interface NavItemProps {
  item: NavigationItem;
  isActive: boolean;
  onNavigate: (path: string) => void;
}

function NavItem({ item, isActive, onNavigate }: NavItemProps) {
  const theme = useAppTheme();
  const { Icon, label, path } = item;

  const iconColor = isActive
    ? theme.colors.purple[100]
    : theme.colors.gray[600];

  return (
    <div css={navItemStyle(theme, isActive)} onClick={() => onNavigate(path)}>
      <Icon color={iconColor} />
      <span css={navTextStyle(theme, isActive)}>{label}</span>
    </div>
  );
}

export default function BottomNavigation() {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useAppTheme();
  const isKeyboardOpen = useKeyboardDetection();

  if (!LAYOUT_CONFIGS[location.pathname]?.bottomNav.show) {
    return null;
  }

  const handleNavigate = (path: string) => {
    navigate(path);
  };

  return (
    <nav
      css={bottomNavStyle(theme)}
      className={isKeyboardOpen ? 'keyboard-open' : ''}
    >
      {NAVIGATION_ITEMS.map((item) => (
        <NavItem
          key={item.id}
          item={item}
          isActive={location.pathname === item.path}
          onNavigate={handleNavigate}
        />
      ))}
    </nav>
  );
}
