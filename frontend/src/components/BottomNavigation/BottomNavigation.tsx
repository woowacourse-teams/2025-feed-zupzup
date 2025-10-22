import { useLocation } from 'react-router-dom';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useKeyboardDetection } from '@/hooks/useKeyboardDetection';
import {
  bottomNavStyle,
  navItemStyle,
  navTextStyle,
} from './BottomNavigation.style';
import {
  NAVIGATION_ITEMS,
  NavigationItem,
} from '../../constants/navigationItems';
import useNavigation from '@/domains/hooks/useNavigation';

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
  const { goPath } = useNavigation();
  const location = useLocation();
  const theme = useAppTheme();
  const isKeyboardOpen = useKeyboardDetection();

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
          onNavigate={goPath}
        />
      ))}
    </nav>
  );
}
