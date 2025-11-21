import type { Meta, StoryObj } from '@storybook/react-webpack5';
import ProgressMenuItem from './ProgressMenuItem';
import DownloadIcon from '@/components/icons/DownloadIcon';

const meta: Meta<typeof ProgressMenuItem> = {
  title: 'Components/ProgressMenuItem',
  component: ProgressMenuItem,
  args: {
    icon: <DownloadIcon />,
    menu: '피드백 추출',
    onClick: () => alert('clicked'),
  },
  parameters: {
    layout: 'centered',
  },
};

export default meta;

type Story = StoryObj<typeof ProgressMenuItem>;

// 기본
export const Default: Story = {
  args: {
    progress: 0,
    disabled: false,
  },
};

// 진행 중 (25%)
export const Progress25: Story = {
  args: {
    progress: 25,
    disabled: true,
  },
};

// 진행 중 (60%)
export const Progress60: Story = {
  args: {
    progress: 60,
    disabled: true,
  },
};

// 완료 직전 (95%)
export const Progress95: Story = {
  args: {
    progress: 95,
    disabled: true,
  },
};

// 완료 (100%)
export const Completed: Story = {
  args: {
    progress: 100,
    disabled: false,
  },
};
