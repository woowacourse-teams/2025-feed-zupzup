import type { Meta, StoryObj } from '@storybook/react-webpack5';
import CategoryTag from './CategoryTag';

const meta: Meta<typeof CategoryTag> = {
  title: 'Common/CategoryTag',
  component: CategoryTag,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      control: 'text',
      description: 'text 따라 카테고리 태그의 내용이 달라집니다.',
    },
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
    text: '기본 값',
    type: 'WAITING',
  },
};

export const IncompleteCategoryTag: Story = {
  args: {
    text: '미완료 태그',
    type: 'WAITING',
  },
};

export const CompleteCategoryTag: Story = {
  args: {
    text: '완료 태그',
    type: 'CONFIRMED',
  },
};
