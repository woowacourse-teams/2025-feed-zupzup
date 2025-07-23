import type { Meta, StoryObj } from '@storybook/react-webpack5';
import StatusTag from './StatusTag';

const meta: Meta<typeof StatusTag> = {
  title: 'Common/StatusTag',
  component: StatusTag,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: 'select',
      options: ['WAITING', 'CONFIRMED'],
      description: '상태에 따라 태그 스타일이 달라집니다.',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    type: 'WAITING',
  },
};

export const Complete: Story = {
  args: {
    type: 'CONFIRMED',
  },
};
