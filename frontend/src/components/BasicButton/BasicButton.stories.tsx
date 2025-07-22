import type { Meta, StoryObj } from '@storybook/react-webpack5';
import BasicButton from './BasicButton';
import PlusIcon from '@/components/icons/PlusIcon';

const meta: Meta<typeof BasicButton> = {
  title: 'Components/BasicButton',
  component: BasicButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    children: {
      control: 'text',
    },
    width: {
      control: 'text',
    },
    variant: {
      control: 'select',
      options: ['primary', 'secondary'],
    },
    onClick: {
      action: 'clicked',
    },
  },
  decorators: [(Story) => <Story />],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  args: {
    children: '기본 버튼',
    variant: 'primary',
  },
};

export const Secondary: Story = {
  args: {
    children: '보조 버튼',
    variant: 'secondary',
  },
};

export const WithIcon: Story = {
  args: {
    children: '아이콘 버튼',
    icon: <PlusIcon />,
    variant: 'primary',
  },
};

export const CustomWidth: Story = {
  args: {
    children: '커스텀 너비',
    width: 200,
    variant: 'primary',
  },
};

export const LongText: Story = {
  args: {
    children: '매우 긴 버튼 텍스트입니다',
    variant: 'primary',
  },
};
