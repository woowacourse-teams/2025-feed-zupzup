import type { Meta, StoryObj } from '@storybook/react-webpack5';
import RoomCategoryTag from './RoomCategoryTag';

const meta: Meta<typeof RoomCategoryTag> = {
  title: 'Common/RoomCategoryTag',
  component: RoomCategoryTag,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    category: {
      control: 'text',
    },
    icon: {
      control: 'text',
    },
    onDeleteClick: {
      action: 'delete clicked',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    category: '칭찬',
    icon: '👍',
    onDeleteClick: () => {},
  },
};
