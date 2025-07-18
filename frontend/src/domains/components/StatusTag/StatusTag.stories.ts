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
      options: ['incomplete', 'complete'],
      description: '상태에 따라 태그 스타일이 달라집니다.',
    },
    children: {
      control: 'text',
      description: '태그 내부에 표시할 텍스트입니다.',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    children: '미완료',
    type: 'incomplete',
  },
};

export const Complete: Story = {
  args: {
    children: '완료',
    type: 'complete',
  },
};
