import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { ThemeProvider } from '@emotion/react';
import { theme } from '@/theme';
import GhostButton from './GhostButton';
import Profile from '@/components/icons/Profile';

const meta: Meta<typeof GhostButton> = {
  title: 'components/GhostButton',
  component: GhostButton,
  tags: ['autodocs'],
  argTypes: {
    onClick: { action: 'clicked' },
  },
  decorators: [
    (Story) => (
      <div style={{ background: '#222', padding: '40px' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof GhostButton>;

export const Default: Story = {
  args: {
    icon: <Profile />,
    text: '고스트 버튼',
  },
};

//아이콘 없는 버전
export const NoIcon: Story = {
  args: {
    text: '고스트 버튼',
  },
};
