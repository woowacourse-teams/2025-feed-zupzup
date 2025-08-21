import { ComponentType } from 'react';
import HomeIcon from '../components/icons/HomeIcon';
import SettingIcon from '../components/icons/SettingIcon';
import { ROUTES } from './routes';

export interface NavigationItem {
  id: string;
  label: string;
  path: string;
  Icon: ComponentType<{ color: string }>;
}

export const NAVIGATION_ITEMS: NavigationItem[] = [
  {
    id: 'home',
    label: '홈',
    path: `${ROUTES.ADMIN}/${ROUTES.ADMIN_HOME}`,
    Icon: HomeIcon,
  },
  {
    id: 'settings',
    label: '설정',
    path: `${ROUTES.ADMIN}/${ROUTES.ADMIN_SETTINGS}`,
    Icon: SettingIcon,
  },
];
