import { ComponentType } from 'react';
import HomeIcon from '../icons/HomeIcon';
import SettingIcon from '../icons/SettingIcon';

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
    path: '/admin-home',
    Icon: HomeIcon,
  },
  {
    id: 'settings',
    label: '설정',
    path: '/admin-settings',
    Icon: SettingIcon,
  },
];
