import type { Meta, StoryObj } from '@storybook/react-webpack5';
import SettingListBox from './SettingListBox';
import OutOutlineIcon from '@/components/icons/OutOutlineIcon';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';

const meta: Meta<typeof SettingListBox> = {
  title: 'Admin/Settings/SettingListBox',
  component: SettingListBox,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    icon: {
      control: false,
      description: '아이콘 컴포넌트',
    },
    title: {
      control: 'text',
      description: '설정 제목',
    },
    description: {
      control: 'text',
      description: '설정 설명',
    },
    variant: {
      control: 'select',
      options: ['default', 'danger'],
      description: '컴포넌트 스타일 variant',
    },
    rightElement: {
      control: false,
      description: '오른쪽에 위치할 요소 (토글, 버튼 등)',
    },
    onClick: {
      action: 'clicked',
      description: '클릭 이벤트 핸들러',
    },
  },
};

export default meta;
type Story = StoryObj<typeof SettingListBox>;

export const NotificationSetting: Story = {
  args: {
    icon: <BellOutlineIcon />,
    title: '알림 설정',
    description: '푸시 알림 및 이메일 설정',
    variant: 'default',
  },
};

export const Logout: Story = {
  args: {
    icon: <OutOutlineIcon />,
    title: '로그아웃',
    variant: 'danger',
  },
};

export const WithToggle: Story = {
  args: {
    icon: <BellOutlineIcon />,
    title: '알림 설정',
    description: '푸시 알림 및 이메일 설정',
    variant: 'default',
    rightElement: (
      <BasicToggleButton
        isToggled={true}
        onClick={() => console.log('토글 클릭됨')}
        name='notification-toggle'
      />
    ),
  },
};
