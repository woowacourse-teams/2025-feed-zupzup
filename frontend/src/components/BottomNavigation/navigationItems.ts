import { ComponentType } from 'react';
import HomeIcon from '../icons/HomeIcon';
import SettingIcon from '../icons/SettingIcon';
import { ROUTES } from '@/constants/routes';

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
    path: ROUTES.ADMIN_HOME,
    Icon: HomeIcon,
  },
  {
    id: 'settings',
    label: '설정',
    path: ROUTES.ADMIN_SETTINGS,
    Icon: SettingIcon,
  },
];
